package app.revanced.manager.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
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

fun Context.requestAllFilesAccess() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        startActivity(Intent(
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
            Uri.fromParts("package", applicationContext.packageName, null)
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addCategory("android.intent.category.DEFAULT")
        })
    }
}

@SuppressLint("BatteryLife")
fun Context.requestIgnoreBatteryOptimizations() {
    startActivity(Intent(
        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
        Uri.fromParts("package", packageName, null)
    ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        addCategory("android.intent.category.DEFAULT")
    })
}

fun Context.isIgnoringOptimizations(): Boolean {
    val pm = getSystemService(ComponentActivity.POWER_SERVICE) as PowerManager
    return pm.isIgnoringBatteryOptimizations(packageName)
}