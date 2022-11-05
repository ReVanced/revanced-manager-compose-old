package app.revanced.manager.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GithubContributor(
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatar_url: String,
    @SerialName("html_url") val url: String,
    @SerialName("contributions") val contributions: Int
)