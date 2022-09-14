package app.revanced.manager.api

import android.util.Log
import app.revanced.manager.dto.github.APIRelease
import app.revanced.manager.preferences.PreferencesManager
import app.revanced.manager.repository.GitHubRepository
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.Json
import java.io.File

val client = HttpClient(Android) {
    BrowserUserAgent()
    install(ContentNegotiation) {
        json(Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

class API(private val repository: GitHubRepository, private val prefs: PreferencesManager) {

    private suspend fun findAsset(repo: String, file: String): PatchesAsset {
        val release = repository.getLatestRelease(repo)
        val asset = release.assets.findAsset(file) ?: throw MissingAssetException()
        return PatchesAsset(release, asset)
    }

    private fun List<APIRelease.Asset>.findAsset(file: String) = find { asset ->
        (asset.name.contains(file) && !asset.name.contains("-sources") && !asset.name.contains("-javadoc"))
    }

    suspend fun downloadPatchBundle(workdir: File): File {
        return try {
            val (_, out) = downloadAsset(workdir, findAsset(prefs.srcPatches.toString(), ".jar"))
            out
        } catch (e: Exception) {
            throw Exception("Failed to download patch bundle", e)
        }
    }

    suspend fun downloadIntegrations(workdir: File): File {
        return try {
            val (_, out) = downloadAsset(
                workdir,
                findAsset(prefs.srcIntegrations.toString(), ".apk")
            )
            out
        } catch (e: Exception) {
            throw Exception("Failed to download integrations", e)
        }
    }

    private suspend fun downloadAsset(
        workdir: File,
        patchesAsset: PatchesAsset
    ): Pair<PatchesAsset, File> {
        val (release, asset) = patchesAsset
        val out = workdir.resolve("${release.tagName}-${asset.name}")
        if (out.exists()) {
            Log.d(
                "ReVanced Manager",
                "Skipping downloading asset ${asset.name} because it exists in cache!"
            )
            return patchesAsset to out
        }
        Log.d("ReVanced Manager", "Downloading asset ${asset.name}")
        client.get(asset.downloadUrl)
            .bodyAsChannel()
            .copyAndClose(out.writeChannel())

        return patchesAsset to out
    }
}
    data class PatchesAsset(
        val release: APIRelease,
        val asset: APIRelease.Asset
    )


class MissingAssetException : Exception()