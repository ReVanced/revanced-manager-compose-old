package app.revanced.manager.network.dto.revanced

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Repositories(
    @SerialName("repositories") val repositories: List<Repository>,
)

@Serializable
class Repository(
    @SerialName("name") val name: String,
    @SerialName("contributors") val contributors: List<Contributor>,
)

@Serializable
class Contributor(
    @SerialName("login") val username: String,
    @SerialName("avatar_url") val avatarUrl: String,
)
