package app.revanced.manager.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.work.*
import app.revanced.manager.installer.service.InstallService
import app.revanced.manager.installer.service.UninstallService
import app.revanced.manager.installer.utils.PM
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.patcher.worker.PatcherWorker
import app.revanced.manager.util.appName
import java.io.File

class PatchingScreenViewModel(
    private val app: Application,
    private val patcherUtils: PatcherUtils,
) : ViewModel() {

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

    private val workManager = WorkManager.getInstance(app)
    var installFailure by mutableStateOf(false)
    var pmStatus by mutableStateOf(-999)
    var extra by mutableStateOf("")

    val outputFile = File(app.cacheDir, "output.apk")

    private val patcherWorker =
        OneTimeWorkRequest.Builder(PatcherWorker::class.java) // create Worker
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST).setInputData(
                Data.Builder().putString("output", outputFile.path).build()
            ).build()

    private val liveData = workManager.getWorkInfoByIdLiveData(patcherWorker.id) // get LiveData

    private val observer = Observer { workInfo: WorkInfo -> // observer for observing patch status
        status = when (workInfo.state) {
            WorkInfo.State.RUNNING -> Status.Patching
            WorkInfo.State.SUCCEEDED -> Status.Success
            WorkInfo.State.FAILED -> Status.Failure
            else -> Status.Idle
        }
    }

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
                PatcherWorker.PATCH_LOG -> {
                    val message = intent.getStringExtra(PatcherWorker.PATCH_MESSAGE)
                    val patchLog =
                        when (intent.getIntExtra(PatcherWorker.PATCH_STATUS, PatcherWorker.INFO)) {
                            PatcherWorker.INFO -> PatchLog.Info(message!!)
                            PatcherWorker.SUCCESS -> PatchLog.Success(message!!)
                            PatcherWorker.ERROR -> PatchLog.Error(message!!)
                            else -> null
                        }
                    patchLog?.let { log(it) }
                }
            }
        }
    }

    init {
        workManager.enqueueUniqueWork("patching", ExistingWorkPolicy.KEEP, patcherWorker)
        liveData.observeForever(observer)
        app.registerReceiver(installBroadcastReceiver, IntentFilter().apply {
            addAction(InstallService.APP_INSTALL_ACTION)
            addAction(UninstallService.APP_UNINSTALL_ACTION)
            addAction(PatcherWorker.PATCH_LOG)
        })
    }

    fun installApk(apk: File) {
        PM.installApp(apk, app)
        log(PatchLog.Info("Installing..."))
    }

    fun postInstallStatus() {
        if (pmStatus == PackageInstaller.STATUS_SUCCESS) {
            log(PatchLog.Success("Successfully installed!"))
            patcherUtils.getSelectedPackageInfo()?.let { saveApp(it) }
        } else {
            installFailure = true
            log(PatchLog.Error("Failed to install!"))
        }
    }

    private fun saveApp(packageInfo: PackageInfo) = patcherUtils.savePatchedApp(
        packageInfo.run {
            PatchedApp(
                appName = app.appName(applicationInfo),
                pkgName = applicationInfo.packageName,
                version = versionName,
                appliedPatches = patcherUtils.selectedPatches
            )
        }
    )

    override fun onCleared() {
        super.onCleared()
        liveData.removeObserver(observer)
        app.unregisterReceiver(installBroadcastReceiver)
        workManager.cancelWorkById(patcherWorker.id)
        logs.clear()
    }

    private fun log(data: PatchLog) {
        logs.add(data)
    }
}