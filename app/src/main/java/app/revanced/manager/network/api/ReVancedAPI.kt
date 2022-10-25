package app.revanced.manager.network.api

import android.util.Log
import app.revanced.manager.network.dto.revanced.Assets
import app.revanced.manager.network.dto.revanced.Repositories
import app.revanced.manager.network.dto.revanced.Tools
import app.revanced.manager.util.body
import app.revanced.manager.util.get
import app.revanced.manager.util.tag
import com.vk.knet.core.Knet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class ReVancedAPI(
    private val client: Knet,
    private val json: Json
) {

    suspend fun ping(): Boolean {
        return try {
            withContext(Dispatchers.Default) {
                client.get("$apiUrl/").statusCode == 200
            }
        } catch (e: Exception) {
            Log.e(tag, "ReVanced API isn't available at the moment. switching to GitHub API")
            false
        }
    }

    suspend fun fetchAssets(): Tools {
        return withContext(Dispatchers.IO) {
            client.get("$apiUrl/tools").body(json)
        }
    }

    suspend fun fetchContributors(): Repositories {
        return withContext(Dispatchers.IO) {
            client.get("$apiUrl/contributors").body(json)
        }
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
        val file = client.get(asset.downloadUrl).body!!.asBytes()
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