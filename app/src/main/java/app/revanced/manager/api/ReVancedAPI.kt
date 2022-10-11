package app.revanced.manager.api

import android.util.Log
import app.revanced.manager.dto.revanced.Assets
import app.revanced.manager.dto.revanced.Repositories
import app.revanced.manager.dto.revanced.Tools
import app.revanced.manager.util.tag
import com.vk.knet.core.Knet
import com.vk.knet.core.http.HttpMethod
import com.vk.knet.core.http.HttpRequest
import com.vk.knet.cornet.CronetKnetEngine
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class ReVancedAPI(cronet: CronetKnetEngine, val json: Json) {

    val client = Knet.Build(cronet)

    suspend fun ping(): Boolean {
        return try {
            withContext(Dispatchers.Default) {
                client.execute(HttpRequest(HttpMethod.GET, "$apiUrl/")).statusCode == 200
            }
        } catch (e: Exception) {
            Log.e(tag, "ReVanced API isn't available at the moment. switching to GitHub API")
            false
        }
    }

    suspend fun fetchAssets() = withContext(Dispatchers.Default) {
        val stream = client.execute(HttpRequest(HttpMethod.GET, "$apiUrl/tools")).body!!.asString()
        json.decodeFromString(stream) as Tools
    }

    suspend fun fetchContributors() = withContext(Dispatchers.Default) {
        val stream = client.execute(HttpRequest(HttpMethod.GET,"$apiUrl/contributors")).body!!.asString()
        json.decodeFromString(stream) as Repositories
    }

    suspend fun findAsset(repo: String, file: String): PatchesAsset {
        val asset = fetchAssets().tools.findAsset(repo, file) ?: throw MissingAssetException()
        return PatchesAsset(asset)
    }

    private fun List<Assets>.findAsset(repo: String, file: String) = find { asset ->
        (asset.name.contains(file) && asset.repository.contains(repo))
    }

    suspend fun downloadAsset(
        workdir: File,
        repo: String,
        name: String,
    ): File {
        val asset = findAsset(repo, name).asset
        val out = workdir.resolve(asset.name)
        if (out.exists()) {
            Log.d(
                tag,
                "Skipping downloading asset ${asset.name} because it exists in cache!"
            )
            return out
        }
        Log.d(tag, "Downloading asset ${asset.name} from ReVanced API.")
        val file = client.execute(
            HttpRequest(
                HttpMethod.GET, asset.downloadUrl
            )
        ).body!!.asBytes()
        out.writeBytes(file)

        return out
    }
    private companion object {
        private const val apiUrl = "https://releases.rvcd.win"
    }
}
data class PatchesAsset(
    val asset: Assets
)


class MissingAssetException : Exception()