package app.revanced.manager.ui.viewmodel

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.Variables.patches
import app.revanced.manager.Variables.selectedAppPackage
import app.revanced.manager.Variables.selectedPatches
import app.revanced.manager.api.GitHubAPI
import app.revanced.manager.api.ReVancedAPI
import app.revanced.manager.preferences.PreferencesManager
import app.revanced.manager.ui.Resource
import app.revanced.manager.util.ghPatches
import app.revanced.manager.util.tag
import app.revanced.patcher.data.Context
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.options
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.Patch
import app.revanced.patcher.util.patch.PatchBundle
import dalvik.system.DexClassLoader
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class PatcherScreenViewModel(
    private val app: Application,
    private val reVancedApi: ReVancedAPI,
    private val gitHubAPI: GitHubAPI,
    private val prefs: PreferencesManager
) : ViewModel() {
    lateinit var patchBundleFile: String

    init {
        viewModelScope.launch {
            loadPatches()
        }
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

    fun setOption(patch: PatchClass, key: String, value: String) {
        patch.patch.options?.set(key, value)
        for (option in patch.patch.options!!) {
            println(option.key + option.value + option.title + option.description)
        }
    }

    fun getOption(patch: PatchClass, key: String) {
        patch.patch.options?.get(key)
    }

    fun isPatchSelected(patchId: String): Boolean {
        return selectedPatches.contains(patchId)
    }

    fun anyPatchSelected(): Boolean {
        return !selectedPatches.isEmpty()
    }


    fun getSelectedPackageInfo(): PackageInfo? {
        return if (selectedAppPackage.value.isPresent) {
            app.packageManager.getPackageArchiveInfo(
                selectedAppPackage.value.get().publicSourceDir,
                PackageManager.GET_META_DATA
            )
        } else {
            null
        }
    }

    fun checkSplitApk(): Boolean {
        if (getSelectedPackageInfo()!!.applicationInfo!!.metaData!!.getBoolean(
                "com.android.vending.splits.required",
                false
            )
        ) {
            Log.d(tag, "APK is split.")
            return true
        }
        Log.d(tag, "APK is not split.")
        return false
    }

    private fun loadPatches() = viewModelScope.launch {
        try {
            val file = if (prefs.srcPatches != ghPatches || !reVancedApi.ping()) {
                gitHubAPI.downloadAsset(app.cacheDir, prefs.srcPatches!!, ".jar")
            } else {
                reVancedApi.downloadAsset(app.cacheDir, prefs.srcPatches!!, ".jar")
            }
            patchBundleFile = file.absolutePath
            loadPatches0()
        } catch (e: Exception) {
            Log.e(tag, "An error occurred while loading patches", e)
        }
    }

    fun loadPatches0() {
        try {
            val patchClasses = PatchBundle.Dex(
                patchBundleFile, DexClassLoader(
                    patchBundleFile,
                    app.codeCacheDir.absolutePath,
                    null,
                    javaClass.classLoader
                )
            ).loadPatches()
            patches.value = Resource.Success(patchClasses)
        } catch (e: Exception) {
            Toast.makeText(app, "Failed to load patch bundle.", Toast.LENGTH_LONG).show()
            Log.e(tag, "Failed to load patch bundle.", e)
            return
        }
        Toast.makeText(app, "Successfully loaded patch bundle.", Toast.LENGTH_LONG).show()
    }

    fun getFilteredPatchesAndCheckOptions(): List<PatchClass> {
        return buildList {
            val selected = getSelectedPackageInfo() ?: return@buildList
            val (patches) = patches.value as? Resource.Success ?: return@buildList
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