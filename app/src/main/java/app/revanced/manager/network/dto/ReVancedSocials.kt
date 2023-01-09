package app.revanced.manager.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ReVancedSocials(
    @SerialName("website") val website: String,
    @SerialName("github") val github: String,
    @SerialName("twitter") val twitter: String,
    @SerialName("discord") val discord: String,
    @SerialName("reddit") val reddit: String,
    @SerialName("telegram") val telegram: String,
    @SerialName("youtube") val youtube: String,
)
