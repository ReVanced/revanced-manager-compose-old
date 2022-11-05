package app.revanced.manager.domain.repository

import app.revanced.manager.network.api.PatchesAsset
import app.revanced.manager.network.dto.GithubContributor
import app.revanced.manager.network.dto.GithubReleases
import app.revanced.manager.network.service.GithubService

interface GithubRepository {
    suspend fun getReleases(repo: String): GithubReleases

    suspend fun getContributors(repo: String): List<GithubContributor>

    suspend fun findAsset(repo: String, file: String): PatchesAsset
}

class GithubRepositoryImpl(
    private val service: GithubService
) : GithubRepository {
    override suspend fun getReleases(repo: String) = service.getReleases(repo)

    override suspend fun getContributors(repo: String) =
        service.getContributors(repo).sortedByDescending(GithubContributor::contributions)

    override suspend fun findAsset(repo: String, file: String) = service.findAsset(repo, file)
}