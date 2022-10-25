package app.revanced.manager.network.api

import android.util.Log
import app.revanced.manager.network.dto.github.Release
import app.revanced.manager.util.body
import app.revanced.manager.util.get
import app.revanced.manager.util.tag
import com.vk.knet.core.Knet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class GitHubAPI(
    private val client: Knet,
    private val json: Json
) {

    private suspend fun findAsset(repo: String, file: String): PatchesAsset {
        val release = getLatestRelease(repo)
        val asset = release.assets.findAsset(file) ?: throw MissingAssetException()
        return PatchesAsset(release, asset)
    }

    private fun List<Release.Asset>.findAsset(file: String) = find { asset ->
        (asset.name.contains(file) && !asset.name.contains("-sources") && !asset.name.contains("-javadoc"))
    }

    suspend fun downloadAsset(
        workdir: File,
        repo: String,
        name: String
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
        Log.d(tag, "Downloading asset ${asset.name} from GitHub API.")
        val file = client.get(asset.downloadUrl).body!!.asBytes()
        out.writeBytes(file)
        return out
    }

    private suspend fun getLatestRelease(repo: String): Release {
        return withContext(Dispatchers.IO) {
            val releases = client.get("$baseUrl/$repo/releases").body<List<Release>>(json)
            releases.first()
        }
    }

    data class PatchesAsset(
        val release: Release,
        val asset: Release.Asset
    )

    private companion object {
        private const val baseUrl = "https://api.github.com/repos"
    }
}