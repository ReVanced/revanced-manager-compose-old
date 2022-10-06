package app.revanced.manager.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.work.*
import app.revanced.manager.patcher.worker.PatcherWorker
import app.revanced.manager.util.tag

class PatchingScreenViewModel(val app: Application) : ViewModel() {

    // create worker
    private val patcherWorker = OneTimeWorkRequest.Builder(PatcherWorker::class.java)
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .setInputData(
            Data.Builder()
                .build()
        ).build()

    var status by mutableStateOf<Status>(Status.Patching)

    sealed class Status {
        object Patching : Status()
        object Success : Status()
        object Failure : Status()
    }


    fun startPatcher() {
        cancelPatching() // cancel existing patching process
        Logging.log = "" // and clear logs

        WorkManager.getInstance(app)
            .enqueueUniqueWork("patching", ExistingWorkPolicy.KEEP, patcherWorker)
        status = Status.Patching // set status
        Log.d(tag, "Created worker.")

        val liveData = WorkManager.getInstance(app).getWorkInfoByIdLiveData(patcherWorker.id) // live data for observing

        liveData.observeForever { workInfo: WorkInfo -> // shitty solution but it works
            status = when (workInfo.state) {
                WorkInfo.State.SUCCEEDED -> Status.Success
                WorkInfo.State.FAILED -> Status.Failure
                else -> Status.Patching
            }
        }

        Log.d(tag, "Worker finished.")
    }

    private fun cancelPatching() {
        WorkManager.getInstance(app).cancelWorkById(patcherWorker.id)
    }
}

object Logging {
    var log by mutableStateOf("")
}