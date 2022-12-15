package app.revanced.manager.ui.viewmodel

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.patcher.ReVancedPatch
import app.revanced.manager.ui.Resource
import app.revanced.patcher.data.Context
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.patch.Patch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

class PatchesSelectorViewModel(
    private val patcherUtils: PatcherUtils
) : ViewModel() {
    val patches = mutableStateListOf<PatchClass>()
    val selectedPatches = patcherUtils.selectedPatches
    var search by mutableStateOf("")
        private set

    fun search(search: String) {
        this.search = search
    }

    fun clearSearch() {
        search = ""
    }

    init {
        viewModelScope.launch { filterPatches() }
    }

    fun isPatchSelected(patch: ReVancedPatch): Boolean {
        return selectedPatches.contains(patch)
    }

    fun selectPatch(patch: ReVancedPatch, state: Boolean) {
        if (state) selectedPatches.add(patch)
        else selectedPatches.remove(patch)
    }

    fun selectAllPatches(patchList: List<PatchClass>, selectAll: Boolean) {
        patchList.forEach { patchClass ->
            val patch = patchClass.patch
            if (selectAll && !patchClass.unsupported) selectedPatches.add(patch)
            else selectedPatches.remove(patch)
        }
    }

    private suspend fun filterPatches() = withContext(Dispatchers.IO) {
        val selected = patcherUtils.getSelectedPackageInfo() ?: return@withContext
        val (patchList) = patcherUtils.patches.value as? Resource.Success ?: return@withContext
        val filtered = buildList {
            patchList.forEach { patch ->
                var unsupported = false
                patch.compatiblePackages?.forEach { pkg ->
                    // if we detect unsupported once, don't overwrite it
                    if (pkg.name == selected.packageName) {
                        if (!unsupported)
                            unsupported =
                                pkg.versions.isNotEmpty() && !pkg.versions.any { it == selected.versionName }
                        add(PatchClass(patch, unsupported))
                    }
                }
            }
        }
        withContext(Dispatchers.Main) {
            patches.addAll(filtered)
        }
    }
}

@Parcelize
data class PatchClass(
    val patch: Class<out Patch<Context>>,
    val unsupported: Boolean,
) : Parcelable