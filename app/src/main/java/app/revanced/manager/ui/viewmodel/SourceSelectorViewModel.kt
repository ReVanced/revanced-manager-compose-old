package app.revanced.manager.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.util.tag
import io.sentry.Sentry
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class SourceSelectorViewModel(val app: Application, private val patcherUtils: PatcherUtils) :
    ViewModel() {
    fun loadBundle(uri: Uri) {
        try {
            val patchesFile = app.cacheDir.resolve("patches.jar")
            Files.copy(
                app.contentResolver.openInputStream(uri),
                patchesFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
            patcherUtils.loadPatchBundle(patchesFile.absolutePath)
        } catch (e: Exception) {
            Log.e(tag, "Failed to load bundle", e)
            Sentry.captureException(e)
        }
    }
}
