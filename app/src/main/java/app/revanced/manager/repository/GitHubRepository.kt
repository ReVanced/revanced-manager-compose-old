package app.revanced.manager.repository

import app.revanced.manager.dto.github.Repositories
import app.revanced.manager.dto.github.Tools
import com.vk.knet.core.Knet
import com.vk.knet.core.http.HttpMethod
import com.vk.knet.core.http.HttpRequest
import com.vk.knet.cornet.CronetKnetEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class GitHubRepository(cronet: CronetKnetEngine) {

    val client = Knet.Build(cronet)

    suspend fun fetchAssets() = withContext(Dispatchers.IO) {
        val stream = client.execute(HttpRequest(HttpMethod.GET, "$apiUrl/tools")).body!!.asString()
        Json.decodeFromString(stream) as Tools
    }

    suspend fun fetchContributors() = withContext(Dispatchers.IO) {
        val stream = client.execute(HttpRequest(HttpMethod.GET,"$apiUrl/contributors")).body!!.asString()
        Json.decodeFromString(stream) as Repositories
    }

    private companion object {
        private const val apiUrl = "https://releases.rvcd.win"
    }
}