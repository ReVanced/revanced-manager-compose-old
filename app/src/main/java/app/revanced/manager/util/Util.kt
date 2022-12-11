package app.revanced.manager.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.drawable.Drawable
import androidx.core.net.toUri


fun Context.openUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
}

fun Context.loadIcon(string: String): Drawable? {
    return try {
        packageManager.getApplicationIcon(string)
    } catch (e: NameNotFoundException) {
        null
    }
}

fun Context.appName(info: ApplicationInfo): String {
    return packageManager.getApplicationLabel(info).toString()
}