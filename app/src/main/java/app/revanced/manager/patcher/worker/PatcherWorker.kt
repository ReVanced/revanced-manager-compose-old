package app.revanced.manager.patcher.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Environment
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import app.revanced.manager.R
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
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class PatcherWorker(
    context: Context,
    parameters: WorkerParameters,
    private val managerAPI: ManagerAPI,
    private val patcherUtils: PatcherUtils
) : CoroutineWorker(context, parameters), KoinComponent {
    private val workdir = createWorkDir()

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(1, createNotification())
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(applicationContext, PatcherWorker::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val channel = NotificationChannel(
            "revanced-patcher-patching", "Patching", NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager =
            ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)
        notificationManager!!.createNotificationChannel(channel)
        return Notification.Builder(applicationContext, channel.id)
            .setContentTitle(applicationContext.getText(R.string.app_name))
            .setContentText(applicationContext.getText(R.string.patcher_notification_message))
            .setLargeIcon(Icon.createWithResource(applicationContext, R.drawable.manager))
            .setSmallIcon(Icon.createWithResource(applicationContext, R.drawable.manager))
            .setContentIntent(pendingIntent).build()
    }

    override suspend fun doWork(): Result {
        if (runAttemptCount > 0) {
            Log.d(tag, "Android requested retrying but retrying is disabled.")
            return Result.failure() // don't retry
        }

        try {
            setForeground(ForegroundInfo(1, createNotification()))
        } catch (e: Exception) {
            Log.d(tag, "Failed to set foreground info:", e)
            Sentry.captureException(e)
        }

        val wakeLock: PowerManager.WakeLock =
            (applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "$tag::Patcher").apply {
                    acquire(10 * 60 * 1000L)
                }
            }
        Log.d(tag, "Acquired wakelock.")

        return try {

            val aaptPath = Aapt.binary(applicationContext)?.absolutePath
            if (aaptPath == null) {
                log("AAPT2 not found.", ERROR)
                throw FileNotFoundException()
            }
            val frameworkPath =
                applicationContext.filesDir.resolve("framework").also { it.mkdirs() }.absolutePath
            val integrationsCacheDir =
                applicationContext.filesDir.resolve("integrations-cache").also { it.mkdirs() }
            val reVancedFolder =
                Environment.getExternalStorageDirectory().resolve("ReVanced").also { it.mkdirs() }
            val appInfo = patcherUtils.selectedAppPackage.value.get()
            val appPath = patcherUtils.selectedAppPackagePath.value

            log("Checking prerequisites...", INFO)
            val patches = patcherUtils.selectedPatches
            if (patches.isEmpty()) throw IllegalStateException("No patches selected.")

            log("Creating directories...", INFO)
            val inputFile = File(workdir, "input.apk")
            val patchedFile = File(workdir, "patched.apk")
            val outputFile = File(workdir, "output.apk")
            val finalFile = reVancedFolder.resolve(appInfo.packageName + ".apk")
            val cacheDirectory = workdir.resolve("cache")

            val integrations = managerAPI.downloadIntegrations(integrationsCacheDir)

            log("Copying APK from device...", INFO)
            withContext(Dispatchers.IO) {
                Files.copy(
                    File(appPath ?: appInfo.publicSourceDir).toPath(),
                    inputFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
            log("Decoding resources", INFO)
            val patcher = Patcher( // start patcher
                PatcherOptions(inputFile,
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

            log("Merging integrations", INFO)
            patcher.addFiles(listOf(integrations)) {}

            val patchesString = if (patches.size > 1) "patches" else "patch"
            log("Applying ${patches.size} $patchesString", INFO)
            patcher.executePatches().forEach { (patch, result) ->
                if (result.isFailure) {
                    log(
                        "Failed to apply $patch: " + "${result.exceptionOrNull()!!.message ?: result.exceptionOrNull()!!::class.simpleName}",
                        ERROR
                    )
                    Log.e(tag, result.exceptionOrNull()!!.stackTraceToString())
                    return@forEach
                }
            }

            log("Saving file", INFO)
            val result = patcher.save() // compile apk

            ZipFile(patchedFile).use { fs ->
                result.dexFiles.forEach {
                    log("Writing dex file ${it.name}", INFO)
                    fs.addEntryCompressData(ZipEntry.createWithName(it.name), it.stream.readBytes())
                }

                log("Aligning apk...", INFO)
                result.resourceFile?.let {
                    fs.copyEntriesFromFileAligned(ZipFile(it), ZipAligner::getEntryAlignment)
                }
                fs.copyEntriesFromFileAligned(ZipFile(inputFile), ZipAligner::getEntryAlignment)
            }

            log("Signing apk...", INFO)
            Signer("ReVanced", "s3cur3p@ssw0rd").signApk(patchedFile, outputFile)
            withContext(Dispatchers.IO) {
                Files.copy(
                    outputFile.inputStream(),
                    finalFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
            log("Successfully patched!", SUCCESS)
            Result.success(Data.Builder().putString(OUTPUT, finalFile.absolutePath).build())

        } catch (e: Exception) {
            log("Error while patching: ${e::class.simpleName}: ${e.message}", ERROR)
            Log.e(tag, e.stackTraceToString())
            Sentry.captureException(e)
            Result.failure()
        } finally {
            Log.d(tag, "Deleting workdir")
            workdir.deleteRecursively()
            wakeLock.release()
            Log.d(tag, "Released wakelock.")
        }
    }


    private fun createWorkDir(): File {
        return applicationContext.cacheDir.resolve("tmp-${System.currentTimeMillis()}")
            .also { it.mkdirs() }
    }

    private fun log(message: String, status: Int) {
        applicationContext.sendBroadcast(Intent().apply {
            action = PATCH_LOG
            putExtra(PATCH_MESSAGE, message)
            putExtra(PATCH_STATUS, status)
        })
    }

    companion object {
        const val PATCH_MESSAGE = "PATCH_MESSAGE"
        const val PATCH_STATUS = "PATCH_STATUS"
        const val PATCH_LOG = "PATCH_LOG"
        const val OUTPUT = "output"

        const val INFO = 0
        const val ERROR = 1
        const val SUCCESS = 2
    }
}