package app.revanced.manager.ui.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.ViewModel
import app.revanced.manager.Variables
import app.revanced.manager.Variables.filteredApps
import app.revanced.manager.Variables.patches
import app.revanced.manager.Variables.selectedAppPackage
import app.revanced.manager.ui.Resource
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import java.util.*

class AppSelectorViewModel(
    val app: Application,
) : ViewModel() {

    init {
        filterApps()
    }

    private fun filterApps(): List<ApplicationInfo> {
        try {
            val (patches) = patches.value as Resource.Success
            patches.forEach patch@{ patch ->
                patch.compatiblePackages?.forEach { pkg ->
                    try {
                        val appInfo = app.packageManager.getApplicationInfo(pkg.name, 0)
                        if (appInfo !in filteredApps) {
                            filteredApps.add(appInfo)
                            return@forEach
                        }
                    } catch (e: Exception) {
                        return@forEach
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ReVanced Manager", "An error occurred while filtering", e)
        }
        return emptyList()
    }

    fun applicationLabel(info: ApplicationInfo): String {
        return app.packageManager.getApplicationLabel(info).toString()
    }

    fun loadIcon(info: ApplicationInfo): Drawable? {
        return info.loadIcon(app.packageManager)
    }

    fun setSelectedAppPackage(appId: String) {
        selectedAppPackage.value.ifPresent { s ->
            if (s != appId) Variables.selectedPatches.clear()
        }
        selectedAppPackage.value = Optional.of(appId)

    }
}