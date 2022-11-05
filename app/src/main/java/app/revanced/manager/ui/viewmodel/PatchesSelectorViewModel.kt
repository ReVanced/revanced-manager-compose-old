package app.revanced.manager.ui.viewmodel

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.ViewModel
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.ui.Resource
import app.revanced.manager.util.tag
import app.revanced.patcher.data.Context
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.options
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.Patch
import kotlinx.parcelize.Parcelize

class PatchesSelectorViewModel(
    private val patcherUtils: PatcherUtils
) : ViewModel() {
    private val selectedPatches = patcherUtils.selectedPatches

    fun isPatchSelected(patchId: String): Boolean {
        return selectedPatches.contains(patchId)
    }

    fun anyPatchSelected(): Boolean {
        return !selectedPatches.isEmpty()
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

    fun getFilteredPatchesAndCheckOptions(): List<PatchClass> {
        return buildList {
            val selected = patcherUtils.getSelectedPackageInfo() ?: return@buildList
            val (patches) = patcherUtils.patches.value as? Resource.Success ?: return@buildList
            patches.forEach patch@{ patch ->
                var unsupported = false
                var hasPatchOptions = false
                if (patch.options != null) {
                    hasPatchOptions = true
                    Log.d(tag, "${patch.patchName} has patch options.")
                }
                patch.compatiblePackages?.forEach { pkg ->
                    // if we detect unsupported once, don't overwrite it
                    if (pkg.name == selected.packageName) {
                        if (!unsupported)
                            unsupported =
                                pkg.versions.isNotEmpty() && !pkg.versions.any { it == selected.versionName }
                        add(PatchClass(patch, unsupported, hasPatchOptions))
                    }
                }
            }
        }
    }
}

@Parcelize
data class PatchClass(
    val patch: Class<out Patch<Context>>,
    val unsupported: Boolean,
    val hasPatchOptions: Boolean,
) : Parcelable