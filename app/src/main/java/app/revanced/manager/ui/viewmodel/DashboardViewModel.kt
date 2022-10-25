package app.revanced.manager.ui.viewmodel

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.network.api.ReVancedAPI
import app.revanced.manager.network.dto.revanced.Assets
import app.revanced.manager.util.ghManager
import app.revanced.manager.util.ghPatcher
import app.revanced.manager.util.tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(private val reVancedApi: ReVancedAPI) : ViewModel() {
    private var _latestPatcherCommit: Assets? by mutableStateOf(null)
    val patcherCommitDate: String
        get() = _latestPatcherCommit?.commitDate ?: "unknown"

    private var _latestManagerCommit: Assets? by mutableStateOf(null)
    val managerCommitDate: String
        get() = _latestManagerCommit?.commitDate ?: "unknown"

    init {
        fetchLastCommit()
    }

    private fun fetchLastCommit() {
        viewModelScope.launch {
            try {
                val repo = withContext(Dispatchers.Default) { reVancedApi.fetchAssets() }
                for (asset in repo.tools) {
                    when (asset.repository) {
                        ghPatcher -> {
                            _latestPatcherCommit = asset
                        }
                        ghManager -> {
                            _latestManagerCommit = asset
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to fetch latest patcher release", e)
            }
        }
    }

    private val Assets.commitDate: String
        get() = DateUtils.getRelativeTimeSpanString(
            formatter.parse(timestamp)!!.time,
            Calendar.getInstance().timeInMillis,
            DateUtils.MINUTE_IN_MILLIS
        ).toString()

    private companion object {
        @SuppressLint("ConstantLocale")
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    }
}