package app.revanced.manager.patcher

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import app.revanced.manager.ui.Resource
import app.revanced.manager.ui.viewmodel.PatchClass
import app.revanced.manager.ui.viewmodel.PatchedApp
import app.revanced.manager.util.reVancedFolder
import app.revanced.manager.util.tag
import app.revanced.patcher.data.Context
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.Patch
import app.revanced.patcher.util.patch.PatchBundle
import dalvik.system.DexClassLoader
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
class PatcherUtils(val app: Application, val json: Json) {
    val patches = mutableStateOf<Resource<List<Class<out Patch<Context>>>>>(Resource.Loading)
    val filteredPatches = mutableStateListOf<PatchClass>()
    val selectedAppPackage = mutableStateOf(Optional.empty<ApplicationInfo>())
    val selectedAppPackagePath = mutableStateOf<String?>(null)
    val selectedPatches = mutableStateListOf<String>()
    val patchedAppsFile = reVancedFolder.resolve("apps.json")
    val patchedApps = mutableStateListOf<PatchedApp>()
    lateinit var patchBundleFile: String

    suspend fun getPatchedApps() = withContext(Dispatchers.IO) {
        if (patchedAppsFile.exists()) {
            val apps: List<PatchedApp> = try {
                json.decodeFromStream(patchedAppsFile.inputStream())
            } catch (e: Exception) {
                Log.e(tag, e.stackTraceToString())
                return@withContext
            }
            apps.forEach { app ->
                if (!patchedApps.any { it.pkgName == app.pkgName }) {
                    patchedApps.add(app)
                }
            }
        }
    }

    fun savePatchedApp(app: PatchedApp) {
        patchedApps.removeIf { it.pkgName == app.pkgName }
        patchedApps.add(app)

        json.encodeToStream(patchedApps as List<PatchedApp>, patchedAppsFile.outputStream())
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
            val path =
                selectedAppPackage.value.get().publicSourceDir ?: selectedAppPackagePath.value
                ?: return null
            app.packageManager.getPackageArchiveInfo(
                path, 1
            )
        } else {
            null
        }
    }

    fun findPatchesByIds(ids: Iterable<String>): List<Class<out Patch<Context>>> {
        val (patches) = patches.value as? Resource.Success ?: return listOf()
        return patches.filter { patch -> ids.any { it == patch.patchName } && patch.compatiblePackages!!.any { it.name == getSelectedPackageInfo()?.packageName } }
    }
}