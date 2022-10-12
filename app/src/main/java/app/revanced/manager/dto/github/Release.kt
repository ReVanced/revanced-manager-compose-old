package app.revanced.manager.dto.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Release(
    val assets: List<Asset>,
) {
    @Serializable
    data class Asset(
        @SerialName("browser_download_url") val downloadUrl: String,
        @SerialName("name") val name: String
    )
}