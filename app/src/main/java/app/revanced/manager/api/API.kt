package app.revanced.manager.api

import android.util.Log
import app.revanced.manager.dto.github.Assets
import app.revanced.manager.preferences.PreferencesManager
import app.revanced.manager.repository.GitHubRepository
import com.vk.knet.core.Knet
import com.vk.knet.core.http.HttpMethod
import com.vk.knet.core.http.HttpRequest
import com.vk.knet.cornet.CronetKnetEngine
import kotlinx.coroutines.*
import java.io.File

class API(private val repository: GitHubRepository, private val prefs: PreferencesManager, cronet: CronetKnetEngine) {

    val client = Knet.Build(cronet)

    suspend fun findAsset(repo: String, file: String): PatchesAsset {
        val asset = repository.fetchAssets().tools.findAsset(repo, file) ?: throw MissingAssetException()
        return PatchesAsset(asset)
    }

    private fun List<Assets>.findAsset(repo: String, file: String) = find { asset ->
        (asset.name.contains(file) && asset.repository.contains(repo))
    }

    suspend fun downloadPatchBundle(workdir: File): File {
        return try {
            withContext(Dispatchers.IO) {
                val (_, out) = downloadAsset(
                    workdir,
                    findAsset(prefs.srcPatches.toString(), ".jar")
                )
                out
            }
        } catch (e: Exception) {
            throw Exception("Failed to download patch bundle", e)
        }
    }
    suspend fun downloadIntegrations(workdir: File): File {
        return try {
            val (_, out) = downloadAsset(
                workdir,
                findAsset(prefs.srcIntegrations.toString(), ".apk")
            )
            out
        } catch (e: Exception) {
            throw Exception("Failed to download integrations", e)
        }
    }

    fun downloadAsset(
        workdir: File,
        assets: PatchesAsset
    ): Pair<PatchesAsset, File> {
        val out = workdir.resolve("${assets.asset.version}-${assets.asset.name}")
        if (out.exists()) {
            Log.d(
                "ReVanced Manager",
                "Skipping downloading asset ${assets.asset.name} because it exists in cache!"
            )
            return assets to out
        }
        Log.d("ReVanced Manager", "Downloading asset ${assets.asset.name}")
            val file = client.execute(
                HttpRequest(
                    HttpMethod.GET,
                    assets.asset.downloadUrl
                )
            ).body!!.asBytes()
            out.writeBytes(file)

        return assets to out
    }
}
data class PatchesAsset(
    val asset: Assets
)


class MissingAssetException : Exception()