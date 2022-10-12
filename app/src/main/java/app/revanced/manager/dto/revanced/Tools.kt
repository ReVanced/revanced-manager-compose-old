package app.revanced.manager.dto.revanced

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Tools(
    @SerialName("tools") val tools: List<Assets>,
)
@Serializable
class Assets(
    @SerialName("repository") val repository: String,
    @SerialName("version") val version: String,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("name") val name: String,
    @SerialName("size") val size: String?,
    @SerialName("browser_download_url") val downloadUrl: String,
    @SerialName("content_type") val content_type: String
)