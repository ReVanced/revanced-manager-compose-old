package app.revanced.manager.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.ui.viewmodel.PatchClass
import app.revanced.manager.ui.viewmodel.PatcherViewModel
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import org.koin.androidx.compose.getViewModel

@Composable
fun PatchCompatibilityDialog(
    patchClass: PatchClass, pvm: PatcherViewModel = getViewModel(), onClose: () -> Unit
) {
    val patch = patchClass.patch
    val packageName = pvm.getSelectedPackageInfo()?.packageName
    AlertDialog(onDismissRequest = onClose, shape = RoundedCornerShape(12.dp), title = {
        Text(stringResource(id = R.string.unsupported), textAlign = TextAlign.Center)
    }, text = {
        (patch.compatiblePackages!!.forEach { p: Package ->
            if (p.name == packageName) {
                Text(
                    stringResource(id = R.string.only_compatible) + p.versions.reversed()
                        .joinToString(", ")
                )
            }
        })
    }, confirmButton = {
        TextButton(onClick = onClose) {
            Text(text = "Dismiss")
        }
    })
}