package app.revanced.manager.network.service

import app.revanced.manager.BuildConfig
import app.revanced.manager.network.api.MissingAssetException
import app.revanced.manager.network.api.PatchesAsset
import app.revanced.manager.network.dto.Assets
import app.revanced.manager.network.dto.ReVancedReleases
import app.revanced.manager.network.dto.ReVancedRepositories
import app.revanced.manager.util.body
import app.revanced.manager.util.get
import com.vk.knet.core.Knet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

interface ReVancedService {
    suspend fun getAssets(): ReVancedReleases

    suspend fun getContributors(): ReVancedRepositories

    suspend fun findAsset(repo: String, file: String): PatchesAsset

    fun List<Assets>.findAsset(repo: String, file: String): Assets?
}

class ReVancedServiceImpl(
    private val client: Knet,
    private val json: Json
) : ReVancedService {
    override suspend fun getAssets(): ReVancedReleases = withContext(Dispatchers.IO) {
        client.get("$apiUrl/tools").body(json)
    }

    override suspend fun getContributors(): ReVancedRepositories = withContext(Dispatchers.IO) {
        client.get("$apiUrl/contributors").body(json)
    }

    override suspend fun findAsset(repo: String, file: String): PatchesAsset {
        val asset = getAssets().tools.findAsset(repo, file) ?: throw MissingAssetException()
        return asset.run { PatchesAsset(downloadUrl, name) }
    }

    override fun List<Assets>.findAsset(repo: String, file: String) = find { asset ->
        (asset.name.contains(file) && asset.repository.contains(repo))
    }

    private companion object {
        private const val apiUrl = BuildConfig.REVANCED_API_URL
    }
}