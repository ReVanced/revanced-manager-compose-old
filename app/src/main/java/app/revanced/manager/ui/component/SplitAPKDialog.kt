package app.revanced.manager.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.revanced.manager.R

@Composable
fun SplitAPKDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, shape = RoundedCornerShape(12.dp),
        title = {
            Text(stringResource(id = R.string.warning), textAlign = TextAlign.Center)
        },
        text = {
            Text(stringResource(R.string.split_apk_warning))
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}