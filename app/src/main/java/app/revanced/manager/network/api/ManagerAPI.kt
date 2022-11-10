package app.revanced.manager.network.api

import android.app.Application
import android.util.Log
import android.webkit.URLUtil
import app.revanced.manager.domain.manager.PreferencesManager
import app.revanced.manager.domain.repository.GithubRepositoryImpl
import app.revanced.manager.domain.repository.ReVancedRepositoryImpl
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.util.ghIntegrations
import app.revanced.manager.util.ghPatches
import app.revanced.manager.util.tag
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ManagerAPI(
    val app: Application,
    private val prefs: PreferencesManager,
    private val patcherUtils: PatcherUtils,
    private val gitHubAPI: GithubRepositoryImpl,
    private val reVancedAPI: ReVancedRepositoryImpl,
    private val client: HttpClient
) {

    private suspend fun downloadAsset(
        workdir: File, downloadUrl: String
    ): File {
        val name = URLUtil.guessFileName(downloadUrl, null, null)
        val out = workdir.resolve(name)
        client.get(downloadUrl).bodyAsChannel().copyAndClose(out.writeChannel())
        return out
    }

    suspend fun downloadPatches() = withContext(Dispatchers.Main) {
        try {
            val asset =
                if (prefs.srcPatches!! == ghPatches) reVancedAPI.findAsset(ghPatches, ".jar")
                else gitHubAPI.findAsset(prefs.srcPatches!!, ".jar")
            asset.run {
                downloadAsset(app.cacheDir, downloadUrl).run {
                    patcherUtils.run {
                        patchBundleFile = absolutePath
                        loadPatchBundle(absolutePath)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "An error occurred while downloading patches", e)
            Sentry.captureException(e)
        }
    }

    suspend fun downloadIntegrations(workdir: File) = withContext(Dispatchers.IO) {
        val asset = if (prefs.srcIntegrations!! == ghIntegrations) {
            reVancedAPI.findAsset(prefs.srcIntegrations!!, ".apk")
        } else gitHubAPI.findAsset(prefs.srcIntegrations!!, ".apk")
        asset.run {
            val file = downloadAsset(workdir, downloadUrl)
            workdir.resolve(name).writeBytes(file.readBytes())
            file
        }
    }
}


data class PatchesAsset(
    val downloadUrl: String, val name: String
)

class MissingAssetException : Exception()