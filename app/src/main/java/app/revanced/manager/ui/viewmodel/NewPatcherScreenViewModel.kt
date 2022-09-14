package app.revanced.manager.ui.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel

class NewPatcherScreenViewModel(
    val app: Application,
) : ViewModel() {
    fun loadIcon(info: ApplicationInfo): Drawable? {
        return info.loadIcon(app.packageManager)
    }
}