package app.revanced.manager.network.api

import android.app.Application
import android.util.Log
import app.revanced.manager.domain.manager.DownloadManager
import app.revanced.manager.domain.manager.PreferencesManager
import app.revanced.manager.domain.repository.GithubRepositoryImpl
import app.revanced.manager.domain.repository.ReVancedRepositoryImpl
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.util.ghIntegrations
import app.revanced.manager.util.ghPatches
import app.revanced.manager.util.tag
import com.vk.knet.core.Knet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ManagerAPI(
    val app: Application,
    private val client: Knet,
    private val prefs: PreferencesManager,
    private val patcherUtils: PatcherUtils,
    private val gitHubAPI: GithubRepositoryImpl,
    private val reVancedAPI: ReVancedRepositoryImpl,
    private val downloadManager: DownloadManager
) {
    private suspend fun downloadPatches(name: String, downloadUrl: String) =
        withContext(Dispatchers.Main) {
            try {
                val asset = if (prefs.srcPatches!! == ghPatches) {
                    reVancedAPI.findAsset(ghPatches, ".jar")
                } else gitHubAPI.findAsset(prefs.srcPatches!!, ".jar")
                asset.run {
                    downloadManager.downloadAsset(app.cacheDir, downloadUrl, name).run {
                        absolutePath.also {
                            patcherUtils.run {
                                patchBundleFile = it
                                loadPatchBundle(it)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "An error occurred while downloading patches", e)
            }
        }

    suspend fun downloadIntegrations(workdir: File) = withContext(Dispatchers.IO) {
        val asset = if (prefs.srcIntegrations!! == ghIntegrations) {
            reVancedAPI.findAsset(prefs.srcIntegrations!!, ".apk")
        } else gitHubAPI.findAsset(prefs.srcIntegrations!!, ".apk")
        asset.run {
            val file = downloadManager.downloadAsset(app.cacheDir, downloadUrl, name)
            workdir.resolve(name).writeBytes(file.readBytes())
            file
        }
    }

}


data class PatchesAsset(
    val downloadUrl: String, val name: String
)

class MissingAssetException : Exception()