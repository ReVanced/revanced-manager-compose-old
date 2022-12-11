package app.revanced.manager.ui.viewmodel

import android.annotation.SuppressLint
import android.text.format.DateUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.domain.repository.ReVancedRepositoryImpl
import app.revanced.manager.network.dto.Assets
import app.revanced.manager.network.utils.getOrNull
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.util.ghManager
import app.revanced.manager.util.ghPatcher
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(
    private val reVancedApi: ReVancedRepositoryImpl,
    private val patcherUtils: PatcherUtils
) : ViewModel() {
    val apps = patcherUtils.patchedApps

    private var _latestPatcherCommit: Assets? by mutableStateOf(null)
    val patcherCommitDate: String
        get() = _latestPatcherCommit?.commitDate ?: "unknown"

    private var _latestManagerCommit: Assets? by mutableStateOf(null)
    val managerCommitDate: String
        get() = _latestManagerCommit?.commitDate ?: "unknown"

    init {
        viewModelScope.launch {
            patcherUtils.getPatchedApps()
            fetchLastCommit()
        }
    }

    private suspend fun fetchLastCommit() {
        val repo = reVancedApi.getAssets().getOrNull() ?: return
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

@Serializable
class PatchedApp(
    val appName: String,
    val pkgName: String,
    val version: String,
    val appliedPatches: List<String>
)
