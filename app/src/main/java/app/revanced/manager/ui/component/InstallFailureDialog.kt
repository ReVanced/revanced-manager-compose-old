package app.revanced.manager.ui.component

import android.content.pm.PackageInstaller
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import app.revanced.manager.R

@Composable
fun InstallFailureDialog(
    onDismiss: () -> Unit, status: Int, result: String
) {
    var showDetails by remember { mutableStateOf(false) }

    val reason = when (status) {
        PackageInstaller.STATUS_FAILURE_BLOCKED -> stringResource(R.string.status_failure_blocked)
        PackageInstaller.STATUS_FAILURE_ABORTED -> stringResource(R.string.status_failure_aborted)
        PackageInstaller.STATUS_FAILURE_CONFLICT -> stringResource(R.string.status_failure_conflict)
        PackageInstaller.STATUS_FAILURE_INCOMPATIBLE -> stringResource(R.string.status_failure_incompatible)
        PackageInstaller.STATUS_FAILURE_INVALID -> stringResource(R.string.status_failure_invalid)
        PackageInstaller.STATUS_FAILURE_STORAGE -> stringResource(R.string.status_failure_storage)
        else -> stringResource(R.string.status_failure)
    }
    if (showDetails) {
        AlertDialog(onDismissRequest = onDismiss, confirmButton = {
            Button(onClick = { showDetails = false }) {
                Text(stringResource(R.string.ok))
            }
        }, title = { Text(stringResource(R.string.details)) }, text = {
            Text(result)
        })
    } else {
        AlertDialog(onDismissRequest = onDismiss, confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        }, dismissButton = {
            OutlinedButton(onClick = { showDetails = true }) {
                Text(stringResource(R.string.details))
            }
        }, title = { Text(stringResource(R.string.install)) }, text = {
            Text(reason)
        })
    }
}