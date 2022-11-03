package app.revanced.manager.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GithubReleases(
    val assets: List<Asset>,
) {
    @Serializable
    data class Asset(
        @SerialName("browser_download_url") val downloadUrl: String,
        @SerialName("name") val name: String
    )
}