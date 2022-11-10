package app.revanced.manager.network.service

import app.revanced.manager.BuildConfig
import app.revanced.manager.network.api.MissingAssetException
import app.revanced.manager.network.api.PatchesAsset
import app.revanced.manager.network.dto.GithubContributor
import app.revanced.manager.network.dto.GithubReleases
import app.revanced.manager.network.utils.APIResponse
import app.revanced.manager.network.utils.getOrNull
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface GithubService {
    suspend fun getReleases(repo: String): APIResponse<GithubReleases>

    suspend fun getContributors(repo: String): APIResponse<List<GithubContributor>>

    suspend fun findAsset(repo: String, file: String): PatchesAsset
}

class GithubServiceImpl(
    private val client: HttpService,
) : GithubService {

    override suspend fun getReleases(repo: String): APIResponse<GithubReleases> {
        return withContext(Dispatchers.IO) {
            client.request {
                url("${baseUrl}/$repo/releases")
            }
        }
    }

    override suspend fun getContributors(repo: String): APIResponse<List<GithubContributor>> {
        return withContext(Dispatchers.IO) {
            client.request {
                url("$baseUrl/$repo/contributors")
            }
        }
    }

    override suspend fun findAsset(repo: String, file: String): PatchesAsset {
        val releases = getReleases(repo).getOrNull() ?: throw Exception("Cannot retrieve assets")
        val asset = releases.assets.find { asset ->
            (asset.name.contains(file) && !asset.name.contains("-sources") && !asset.name.contains("-javadoc"))
        } ?: throw MissingAssetException()
        return asset.run { PatchesAsset(downloadUrl, name) }
    }
    companion object {
        private const val baseUrl = BuildConfig.GITHUB_API_URL
    }
}