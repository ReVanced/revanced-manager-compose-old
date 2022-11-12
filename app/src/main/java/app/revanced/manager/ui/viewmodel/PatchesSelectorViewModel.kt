package app.revanced.manager.ui.viewmodel

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.ui.Resource
import app.revanced.patcher.data.Context
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.Patch
import kotlinx.parcelize.Parcelize

class PatchesSelectorViewModel(
    private val patcherUtils: PatcherUtils
) : ViewModel() {
    val filteredPatches = patcherUtils.filteredPatches
    val selectedPatches = patcherUtils.selectedPatches
    var loading by mutableStateOf(true)

    fun isPatchSelected(patchId: String): Boolean {
        return selectedPatches.contains(patchId)
    }

    fun selectPatch(patchId: String, state: Boolean) {
        if (state) selectedPatches.add(patchId)
        else selectedPatches.remove(patchId)
    }

    fun selectAllPatches(patchList: List<PatchClass>, selectAll: Boolean) {
        patchList.forEach { patch ->
            val patchId = patch.patch.patchName
            if (selectAll && !patch.unsupported) selectedPatches.add(patchId)
            else selectedPatches.remove(patchId)
        }
    }

    fun filterPatches() {
        loading = true
        val selected = patcherUtils.getSelectedPackageInfo() ?: return
        val (patches) = patcherUtils.patches.value as? Resource.Success ?: return
        if (filteredPatches.isNotEmpty()) {
            loading = false; return
        }
        patches.forEach patch@{ patch ->
            var unsupported = false
            patch.compatiblePackages?.forEach { pkg ->
                // if we detect unsupported once, don't overwrite it
                if (pkg.name == selected.packageName) {
                    if (!unsupported)
                        unsupported =
                            pkg.versions.isNotEmpty() && !pkg.versions.any { it == selected.versionName }
                    filteredPatches.add(PatchClass(patch, unsupported))
                }
            }
        }
        loading = false
    }
}

@Parcelize
data class PatchClass(
    val patch: Class<out Patch<Context>>,
    val unsupported: Boolean,
) : Parcelable