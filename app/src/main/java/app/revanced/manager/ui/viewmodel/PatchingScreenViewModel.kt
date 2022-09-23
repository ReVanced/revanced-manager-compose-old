package app.revanced.manager.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import app.revanced.manager.patcher.worker.PatcherWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PatchingScreenViewModel(val app: Application) : ViewModel() {

    var patchingInProgress = true

    init {
        viewModelScope.launch(Dispatchers.Main) {
            startPatcher()
        }
    }

    private fun startPatcher() {
        WorkManager
            .getInstance(app)
            .enqueueUniqueWork(
                "patching",
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequest.Builder(PatcherWorker::class.java)
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setInputData(
                        androidx.work.Data.Builder()
                            .build()
                    ).build()
            )
        patchingInProgress = false
    }
}

object Logging {
    var log by mutableStateOf("")
}