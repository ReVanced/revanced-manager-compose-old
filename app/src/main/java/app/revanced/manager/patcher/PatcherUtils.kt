package app.revanced.manager.patcher

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import app.revanced.manager.ui.Resource
import app.revanced.manager.ui.viewmodel.PatchClass
import app.revanced.manager.util.tag
import app.revanced.patcher.data.Context
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.Patch
import app.revanced.patcher.util.patch.PatchBundle
import dalvik.system.DexClassLoader
import io.sentry.Sentry
import java.util.*

class PatcherUtils(val app: Application) {
    val patches = mutableStateOf<Resource<List<Class<out Patch<Context>>>>>(Resource.Loading)
    val filteredPatches = mutableStateListOf<PatchClass>()
    val selectedAppPackage = mutableStateOf(Optional.empty<ApplicationInfo>())
    val selectedAppPackagePath = mutableStateOf<String?>(null)
    val selectedPatches = mutableStateListOf<String>()
    lateinit var patchBundleFile: String

    fun cleanup() {
        patches.value = Resource.Loading
        selectedAppPackage.value = Optional.empty()
        selectedPatches.clear()
    }

    fun loadPatchBundle(file: String? = patchBundleFile) {
        try {
            if (this::patchBundleFile.isInitialized) {
                val patchClasses = PatchBundle.Dex(
                    file!!, DexClassLoader(
                        file, app.codeCacheDir.absolutePath, null, javaClass.classLoader
                    )
                ).loadPatches()
                patches.value = Resource.Success(patchClasses)
            } else throw IllegalStateException("No patch bundle(s) selected.")
        } catch (e: Exception) {
            Log.e(tag, "Failed to load patch bundle.", e)
            Sentry.captureException(e)
        }
    }

    fun getSelectedPackageInfo(): PackageInfo? {
        return if (selectedAppPackage.value.isPresent) {
            val path = selectedAppPackage.value.get().publicSourceDir ?: selectedAppPackagePath.value ?: return null
            app.packageManager.getPackageArchiveInfo(
                path, 1
            )
        } else {
            null
        }
    }

    fun findPatchesByIds(ids: Iterable<String>): List<Class<out Patch<Context>>> {
        val (patches) = patches.value as? Resource.Success ?: return listOf()
        return patches.filter { patch -> ids.any { it == patch.patchName } }
    }
}