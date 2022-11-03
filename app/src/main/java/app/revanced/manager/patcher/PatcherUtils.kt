package app.revanced.manager.patcher

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import app.revanced.manager.ui.Resource
import app.revanced.manager.util.tag
import app.revanced.patcher.data.Context
import app.revanced.patcher.patch.Patch
import app.revanced.patcher.util.patch.PatchBundle
import dalvik.system.DexClassLoader
import java.util.*

class PatcherUtils(val app: Application) {
    val patches = mutableStateOf<Resource<List<Class<out Patch<Context>>>>>(Resource.Loading)
    val selectedAppPackage = mutableStateOf(Optional.empty<ApplicationInfo>())
    val selectedPatches = mutableStateListOf<String>()
    lateinit var patchBundleFile: String

    fun loadPatchBundle(file: String? = patchBundleFile) {
        try {
            val patchClasses = PatchBundle.Dex(
                file!!, DexClassLoader(
                    file,
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
}