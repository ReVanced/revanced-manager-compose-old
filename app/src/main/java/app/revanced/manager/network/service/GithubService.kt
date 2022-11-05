package app.revanced.manager.network.service

import app.revanced.manager.BuildConfig
import app.revanced.manager.network.api.MissingAssetException
import app.revanced.manager.network.api.PatchesAsset
import app.revanced.manager.network.dto.GithubContributor
import app.revanced.manager.network.dto.GithubReleases
import app.revanced.manager.util.body
import app.revanced.manager.util.get
import com.vk.knet.core.Knet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

interface GithubService {
    suspend fun getReleases(repo: String): GithubReleases

    suspend fun getContributors(repo: String): List<GithubContributor>

    suspend fun findAsset(repo: String, file: String): PatchesAsset

    fun List<GithubReleases.Asset>.findAsset(file: String): GithubReleases.Asset?
}

class GithubServiceImpl(
    private val client: Knet,
    private val json: Json
) : GithubService {

    override suspend fun getReleases(repo: String): GithubReleases = withContext(Dispatchers.IO) {
        client.get("${baseUrl}/$repo/releases").body(json)
    }

    override suspend fun getContributors(repo: String): List<GithubContributor> =
        withContext(Dispatchers.IO) {
            client.get("$baseUrl/$repo/contributors").body(json)
        }

    override suspend fun findAsset(repo: String, file: String): PatchesAsset {
        val release = getReleases(repo)
        val asset = release.assets.findAsset(file) ?: throw MissingAssetException()
        return asset.run { PatchesAsset(downloadUrl, name) }
    }

    override fun List<GithubReleases.Asset>.findAsset(file: String) = find { asset ->
        (asset.name.contains(file) && !asset.name.contains("-sources") && !asset.name.contains("-javadoc"))
    }

    companion object {
        private const val baseUrl = BuildConfig.GITHUB_API_URL
    }
}