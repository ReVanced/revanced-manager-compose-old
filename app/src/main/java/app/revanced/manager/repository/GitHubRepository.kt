package app.revanced.manager.repository

import app.revanced.manager.dto.github.Tools
import app.revanced.manager.dto.github.Repositories
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GitHubRepository(val client: HttpClient) {


    suspend fun fetchAssets() = withContext(Dispatchers.IO) {
        client.get("$apiUrl/tools") {
            parameter("per_page", 1)
        }.body() as Tools
    }

    suspend fun fetchContributors() = withContext(Dispatchers.IO) {
        client.get("$apiUrl/contributors").body() as Repositories
    }

    private companion object {
        private const val apiUrl = "https://releases.rvcd.win"
    }
}