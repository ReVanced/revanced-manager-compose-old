package app.revanced.manager.ui.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.ui.Resource
import app.revanced.manager.util.tag
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class AppSelectorViewModel(
    val app: Application, patcherUtils: PatcherUtils
) : ViewModel() {

    val filteredApps = mutableStateListOf<ApplicationInfo>()
    val patches = patcherUtils.patches
    private val filteredPatches = patcherUtils.filteredPatches
    private val selectedAppPackage = patcherUtils.selectedAppPackage
    private val selectedAppPackagePath = patcherUtils.selectedAppPackagePath
    private val selectedPatches = patcherUtils.selectedPatches

    init {
        viewModelScope.launch { filterApps() }
    }

    private suspend fun filterApps() = withContext(Dispatchers.Default) {
        try {
            val (patches) = patches.value as Resource.Success
            patches.forEach patch@{ patch ->
                patch.compatiblePackages?.forEach { pkg ->
                    try {
                        if (!(filteredApps.any { it.packageName == pkg.name })) {
                            val appInfo = app.packageManager.getApplicationInfo(pkg.name, 1)
                            filteredApps.add(appInfo)
                            return@forEach
                        }
                    } catch (e: Exception) {
                        return@forEach
                    }
                }
            }
            Log.d(tag, "Filtered apps.")
        } catch (e: Exception) {
            Log.e(tag, "An error occurred while filtering", e)
            Sentry.captureException(e)
        }
    }

    fun applicationLabel(info: ApplicationInfo): String {
        return app.packageManager.getApplicationLabel(info).toString()
    }

    fun loadIcon(info: ApplicationInfo): Drawable {
        return app.packageManager.getApplicationIcon(info)
    }

    fun setSelectedAppPackage(appId: ApplicationInfo, appPackagePath: String? = null) {
        selectedAppPackage.value.ifPresent { s ->
            if (s != appId) {
                selectedPatches.clear()
                filteredPatches.clear()
            }
        }
        selectedAppPackage.value = Optional.of(appId)
        selectedAppPackagePath.value = appPackagePath
    }

    fun setSelectedAppPackageFromFile(file: Uri?) {
        try {
            val apkDir = app.cacheDir.resolve(File(file!!.path!!).name)
            app.contentResolver.openInputStream(file)!!.run {
                copyTo(apkDir.outputStream())
                close()
            }
            setSelectedAppPackage(
                app.packageManager.getPackageArchiveInfo(
                    apkDir.path, 1
                )!!.applicationInfo,
                apkDir.absolutePath
            )
        } catch (e: Exception) {
            Log.e(tag, "Failed to load apk", e)
            Sentry.captureException(e)
        }
    }
}