package app.revanced.manager.installer.nonroot

import android.content.Context
import app.revanced.manager.installer.nonroot.utils.PM
import java.io.File

object NonRootAppInstaller {
    fun installApp(apk: File, context: Context) {
        PM.installApp(apk, context)
    }
}