package app.revanced.manager.ui.component

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import app.revanced.manager.BuildConfig
import app.revanced.manager.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun PermissionsDialog() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        AllFilesAccess()
    } else {
        StoragePermission()
    }
}

@SuppressLint("NewApi")
@Composable
fun AllFilesAccess() {
    var hasAccess by remember { mutableStateOf(Environment.isExternalStorageManager()) }
    if (!hasAccess) {
        AlertDialog(onDismissRequest = {}, shape = RoundedCornerShape(12.dp), title = {
            Text(stringResource(id = R.string.permissions))
        }, text = {
            Text(stringResource(R.string.permission_request))
        }, confirmButton = {
            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (Environment.isExternalStorageManager()) {
                        hasAccess = true
                    }
                }
            TextButton(onClick = {
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    .setData("package:${BuildConfig.APPLICATION_ID}".toUri())
                    .let { launcher.launch(it) }
            }) {
                Text(text = stringResource(R.string.ok))
            }
        })
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StoragePermission() {
    val permission = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )
    if (!permission.allPermissionsGranted) {
        AlertDialog(onDismissRequest = {}, shape = RoundedCornerShape(12.dp), title = {
            Text(stringResource(id = R.string.permissions))
        }, text = {
            R.string.permission_request
        }, confirmButton = {
            TextButton(onClick = permission::launchMultiplePermissionRequest) {
                Text(text = stringResource(R.string.ok))
            }
        }, properties = DialogProperties(
            dismissOnBackPress = false, dismissOnClickOutside = false
        )
        )
    }
}