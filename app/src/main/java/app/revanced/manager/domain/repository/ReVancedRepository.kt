package app.revanced.manager.domain.repository

import app.revanced.manager.network.api.PatchesAsset
import app.revanced.manager.network.dto.ReVancedReleases
import app.revanced.manager.network.dto.ReVancedRepositories
import app.revanced.manager.network.service.ReVancedService

interface ReVancedRepository {
    suspend fun getAssets(): ReVancedReleases

    suspend fun getContributors(): ReVancedRepositories

    suspend fun findAsset(file: String, repo: String): PatchesAsset
}

class ReVancedRepositoryImpl(
    private val service: ReVancedService
) : ReVancedRepository {
    override suspend fun getAssets() = service.getAssets()

    override suspend fun getContributors() = service.getContributors()

    override suspend fun findAsset(file: String, repo: String) = service.findAsset(file, repo)
}