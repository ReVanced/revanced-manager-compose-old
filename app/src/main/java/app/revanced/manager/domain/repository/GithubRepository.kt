package app.revanced.manager.domain.repository

import app.revanced.manager.network.api.PatchesAsset
import app.revanced.manager.network.dto.GithubContributor
import app.revanced.manager.network.dto.GithubReleases
import app.revanced.manager.network.service.GithubService
import app.revanced.manager.network.utils.APIResponse

interface GithubRepository {
    suspend fun getReleases(repo: String): APIResponse<GithubReleases>

    suspend fun getContributors(repo: String): APIResponse<List<GithubContributor>>

    suspend fun findAsset(repo: String, file: String): PatchesAsset
}

class GithubRepositoryImpl(
    private val service: GithubService
) : GithubRepository {
    override suspend fun getReleases(repo: String) = service.getReleases(repo)

    override suspend fun getContributors(repo: String) = service.getContributors(repo)

    override suspend fun findAsset(repo: String, file: String) = service.findAsset(repo, file)
}