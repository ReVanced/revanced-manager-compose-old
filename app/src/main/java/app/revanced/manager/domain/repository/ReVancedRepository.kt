package app.revanced.manager.domain.repository

import app.revanced.manager.network.api.PatchesAsset
import app.revanced.manager.network.dto.ReVancedReleases
import app.revanced.manager.network.dto.ReVancedRepositories
import app.revanced.manager.network.service.ReVancedService
import app.revanced.manager.network.utils.APIResponse

interface ReVancedRepository {
    suspend fun getAssets(): APIResponse<ReVancedReleases>

    suspend fun getContributors(): APIResponse<ReVancedRepositories>

    suspend fun findAsset(repo: String, file: String): PatchesAsset
}

class ReVancedRepositoryImpl(
    private val service: ReVancedService
) : ReVancedRepository {
    override suspend fun getAssets() = service.getAssets()

    override suspend fun getContributors() = service.getContributors()

    override suspend fun findAsset(repo: String, file: String) = service.findAsset(repo, file)
}