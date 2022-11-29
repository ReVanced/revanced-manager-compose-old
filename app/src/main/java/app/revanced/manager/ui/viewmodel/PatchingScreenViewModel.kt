package app.revanced.manager.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.os.Environment
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.installer.service.InstallService
import app.revanced.manager.installer.service.UninstallService
import app.revanced.manager.installer.utils.PM
import app.revanced.manager.network.api.ManagerAPI
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.patcher.aapt.Aapt
import app.revanced.manager.patcher.aligning.ZipAligner
import app.revanced.manager.patcher.aligning.zip.ZipFile
import app.revanced.manager.patcher.aligning.zip.structures.ZipEntry
import app.revanced.manager.patcher.signing.Signer
import app.revanced.manager.util.tag
import app.revanced.patcher.Patcher
import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.logging.Logger
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.CancellationException

class PatchingScreenViewModel(
    private val app: Application,
    private val managerAPI: ManagerAPI,
    private val patcherUtils: PatcherUtils
) : ViewModel() {

    var installFailure by mutableStateOf(false)

    var pmStatus by mutableStateOf(-999)
    var extra by mutableStateOf("")

    sealed interface PatchLog {
        val message: String

        data class Success(override val message: String) : PatchLog
        data class Info(override val message: String) : PatchLog
        data class Error(override val message: String) : PatchLog
    }

    sealed class Status {
        object Idle : Status()
        object Patching : Status()
        object Success : Status()
        object Failure : Status()
    }

    val outputFile = File(app.filesDir, "output.apk")
    val logs = mutableStateListOf<PatchLog>()
    var status by mutableStateOf<Status>(Status.Idle)

    private val installBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                InstallService.APP_INSTALL_ACTION -> {
                    pmStatus = intent.getIntExtra(InstallService.EXTRA_INSTALL_STATUS, -999)
                    extra = intent.getStringExtra(InstallService.EXTRA_INSTALL_STATUS_MESSAGE)!!
                    postInstallStatus()
                }
                UninstallService.APP_UNINSTALL_ACTION -> {
                }
            }
        }
    }

    init {
        status = Status.Patching
        app.registerReceiver(
            installBroadcastReceiver,
            IntentFilter().apply {
                addAction(InstallService.APP_INSTALL_ACTION)
                addAction(UninstallService.APP_UNINSTALL_ACTION)
            }
        )
    }

    fun installApk(apk: File) {
        PM.installApp(apk, app)
        log(PatchLog.Info("Installing..."))
    }

    fun postInstallStatus() {
        if (pmStatus == PackageInstaller.STATUS_SUCCESS) {
            log(PatchLog.Success("Successfully installed!"))
        } else {
            installFailure = true
            log(PatchLog.Error("Failed to install!"))
        }
    }

    private val patcher = viewModelScope.launch(Dispatchers.IO) {
        val workdir = createWorkDir()
        val wakeLock: PowerManager.WakeLock =
            (app.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "$tag::Patcher").apply {
                    acquire(10 * 60 * 1000L)
                }
            }
        Log.d(tag, "Acquired wakelock.")
        try {
            val aaptPath = Aapt.binary(app)?.absolutePath
            if (aaptPath == null) {
                log(PatchLog.Error("AAPT2 not found."))
                throw FileNotFoundException()
            }
            val frameworkPath = app.filesDir.resolve("framework").also { it.mkdirs() }.absolutePath
            val integrationsCacheDir =
                app.filesDir.resolve("integrations-cache").also { it.mkdirs() }
            val reVancedFolder =
                Environment.getExternalStorageDirectory().resolve("ReVanced").also { it.mkdirs() }
            val appInfo = patcherUtils.selectedAppPackage.value.get()
            val appPath = patcherUtils.selectedAppPackagePath.value

            log(PatchLog.Info("Checking prerequisites..."))
            val patches = patcherUtils.findPatchesByIds(patcherUtils.selectedPatches)
            if (patches.isEmpty()) throw IllegalStateException("No patches selected.")

            log(PatchLog.Info("Creating directories..."))
            val inputFile = File(app.filesDir, "input.apk")
            val patchedFile = File(workdir, "patched.apk")
            val cacheDirectory = workdir.resolve("cache")

            val integrations = managerAPI.downloadIntegrations(integrationsCacheDir)

            log(PatchLog.Info("Copying APK from device..."))
            withContext(Dispatchers.IO) {
                Files.copy(
                    File(appPath ?: appInfo.publicSourceDir).toPath(),
                    inputFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
            log(PatchLog.Info("Decoding resources"))
            val patcher = Patcher( // start patcher
                PatcherOptions(
                    inputFile,
                    cacheDirectory.absolutePath,
                    aaptPath = aaptPath,
                    frameworkFolderLocation = frameworkPath,
                    logger = object : Logger {
                        override fun error(msg: String) {
                            Log.e(tag, msg)
                        }

                        override fun warn(msg: String) {
                            Log.w(tag, msg)
                        }

                        override fun info(msg: String) {
                            Log.i(tag, msg)
                        }

                        override fun trace(msg: String) {
                            Log.v(tag, msg)
                        }
                    })
            )


            Log.d(tag, "Adding ${patches.size} patch(es)")
            patcher.addPatches(patches)

            log(PatchLog.Info("Merging integrations"))
            patcher.addFiles(listOf(integrations)) {}

            val patchesString = if (patches.size > 1) "patches" else "patch"
            log(PatchLog.Info("Applying ${patches.size} $patchesString"))
            patcher.executePatches().forEach { (patch, result) ->
                if (result.isFailure) {
                    log(PatchLog.Info("Failed to apply $patch: " + "${result.exceptionOrNull()!!.message ?: result.exceptionOrNull()!!::class.simpleName}"))
                    Log.e(tag, result.exceptionOrNull()!!.stackTraceToString())
                    return@forEach
                }
            }

            log(PatchLog.Info("Saving file"))
            val result = patcher.save() // compile apk

            ZipFile(patchedFile).use { fs ->
                result.dexFiles.forEach {
                    log(PatchLog.Info("Writing dex file ${it.name}"))
                    fs.addEntryCompressData(ZipEntry.createWithName(it.name), it.stream.readBytes())
                }

                log(PatchLog.Info("Aligning apk..."))
                result.resourceFile?.let {
                    fs.copyEntriesFromFileAligned(ZipFile(it), ZipAligner::getEntryAlignment)
                }
                fs.copyEntriesFromFileAligned(ZipFile(inputFile), ZipAligner::getEntryAlignment)
            }

            log(PatchLog.Info("Signing apk..."))
            Signer("ReVanced", "s3cur3p@ssw0rd").signApk(patchedFile, outputFile)
            withContext(Dispatchers.IO) {
                Files.copy(
                    outputFile.inputStream(),
                    reVancedFolder.resolve(appInfo.packageName + ".apk").toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
            log(PatchLog.Success("Successfully patched!"))
            patcherUtils.cleanup()
            status = Status.Success
        } catch (e: Exception) {
            status = Status.Failure
            Log.e(tag, "Error while patching: ${e.message ?: e::class.simpleName}")
            Sentry.captureException(e)
        }
        Log.d(tag, "Deleting workdir")
        workdir.deleteRecursively()
        wakeLock.release()
        Log.d(tag, "Released wakelock.")
    }

    override fun onCleared() {
        super.onCleared()
        app.unregisterReceiver(installBroadcastReceiver)
        patcher.cancel(CancellationException("ViewModel cleared"))
        logs.clear()
    }

    private fun createWorkDir(): File {
        return app.cacheDir.resolve("tmp-${System.currentTimeMillis()}").also { it.mkdirs() }
    }

    private fun log(data: PatchLog) {
        logs.add(data)
    }
}