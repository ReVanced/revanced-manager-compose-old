package app.revanced.manager.ui.viewmodel

import  android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.work.*
import app.revanced.manager.patcher.worker.PatcherWorker

class PatchingScreenViewModel(val app: Application) : ViewModel() {

    private val patcherWorker =
        OneTimeWorkRequest.Builder(PatcherWorker::class.java) // create Worker
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                Data.Builder()
                    .build()
            ).build()

    private val liveData =
        WorkManager.getInstance(app).getWorkInfoByIdLiveData(patcherWorker.id) // get LiveData

    private val observer = Observer { workInfo: WorkInfo -> // observer for observing patch status
        status = when (workInfo.state) {
            WorkInfo.State.RUNNING -> Status.Patching
            WorkInfo.State.SUCCEEDED -> Status.Success
            WorkInfo.State.FAILED -> Status.Failure
            else -> Status.Idle
        }
    }

    var status by mutableStateOf<Status>(Status.Idle)

    sealed class Status {
        object Idle : Status()
        object Patching : Status()
        object Success : Status()
        object Failure : Status()
    }

    fun startPatcher() {
        cancelPatching() //  cancel patching if its still running
        Logging.log = "" // clear logs

        WorkManager.getInstance(app)
            .enqueueUniqueWork(
                "patching",
                ExistingWorkPolicy.KEEP,
                patcherWorker
            ) // enqueue patching process
        liveData.observeForever(observer) // start observing patch status
    }

    private fun cancelPatching() {
        WorkManager.getInstance(app).cancelWorkById(patcherWorker.id)
    }

    override fun onCleared() {
        super.onCleared()
        liveData.removeObserver(observer) // remove observer when ViewModel is destroyed
    }
}

object Logging {
    var log by mutableStateOf("")
}