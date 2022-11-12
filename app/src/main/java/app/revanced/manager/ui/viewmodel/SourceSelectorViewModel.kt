package app.revanced.manager.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.util.tag
import io.sentry.Sentry
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class SourceSelectorViewModel(val app: Application, val patcherUtils: PatcherUtils) : ViewModel() {
    fun loadBundle(uri: Uri) {
        try {
            val patchesFile = app.cacheDir.resolve(File(uri.path!!).name)
            Files.copy(
                app.contentResolver.openInputStream(uri),
                patchesFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
            patchesFile.absolutePath.also {
                patcherUtils.patchBundleFile = it
                patcherUtils.loadPatchBundle(it)
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to load bundle", e)
            Sentry.captureException(e)
        }
    }
}