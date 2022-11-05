package app.revanced.manager.network.api

import android.app.Application
import android.util.Log
import android.webkit.URLUtil
import app.revanced.manager.domain.manager.PreferencesManager
import app.revanced.manager.domain.repository.GithubRepositoryImpl
import app.revanced.manager.domain.repository.ReVancedRepositoryImpl
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.util.get
import app.revanced.manager.util.ghIntegrations
import app.revanced.manager.util.ghPatches
import app.revanced.manager.util.tag
import com.vk.knet.core.Knet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ManagerAPI(
    val app: Application,
    private val prefs: PreferencesManager,
    private val patcherUtils: PatcherUtils,
    private val gitHubAPI: GithubRepositoryImpl,
    private val reVancedAPI: ReVancedRepositoryImpl,
    private val client: Knet
) {

    private fun downloadAsset(
        workdir: File, downloadUrl: String
    ): File {
        val name = URLUtil.guessFileName(downloadUrl, null, null)
        val out = workdir.resolve(name)
        if (out.exists()) {
            Log.d(
                tag, "Skipping downloading asset $name because it exists in cache!"
            )
            return out
        }
        val file = client.get(downloadUrl).body!!.asBytes()
        out.writeBytes(file)
        return out
    }

    suspend fun downloadPatches() =
        withContext(Dispatchers.Main) {
            try {
                val asset = if (prefs.srcPatches!! == ghPatches) {
                    reVancedAPI.findAsset(ghPatches, ".jar")
                } else gitHubAPI.findAsset(prefs.srcPatches!!, ".jar")
                asset.run {
                    downloadAsset(app.cacheDir, downloadUrl).run {
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
            val file = downloadAsset(File(name), downloadUrl)
            workdir.resolve(name).writeBytes(file.readBytes())
            file
        }
    }

}


data class PatchesAsset(
    val downloadUrl: String, val name: String
)

class MissingAssetException : Exception()