package app.revanced.manager.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.Variables.patches
import app.revanced.manager.Variables.selectedAppPackage
import app.revanced.manager.Variables.selectedPatches
import app.revanced.manager.ui.Resource
import app.revanced.manager.ui.component.FloatingActionButton
import app.revanced.manager.ui.component.SplitAPKDialog
import app.revanced.manager.ui.viewmodel.PatcherScreenViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatcherScreen(
    onClickAppSelector: () -> Unit,
    onClickPatchSelector: () -> Unit,
    onClickPatch: () -> Unit,
    onClickSourceSelector: () -> Unit,
    viewModel: PatcherScreenViewModel = getViewModel()
) {
    val selectedAmount = selectedPatches.size
    val selectedAppPackage by selectedAppPackage
    val hasAppSelected = selectedAppPackage.isPresent
    val patchesLoaded = patches.value is Resource.Success
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                enabled = hasAppSelected && viewModel.anyPatchSelected(),
                onClick = {
                    if (viewModel.checkSplitApk()) {
                        showDialog = true
                    } else {
                        onClickPatch(); viewModel.loadPatches0()
                    }
                }, // TODO: replace this with something better
                icon = { Icon(Icons.Default.Build, contentDescription = "Patch") },
                text = { Text(text = "Patch") }
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            if (showDialog)
                SplitAPKDialog(onDismiss = { showDialog = false }, onConfirm = onClickPatch)
            ElevatedCard(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                onClick = onClickSourceSelector
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.select_sources),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            ElevatedCard(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                enabled = patchesLoaded,
                onClick = onClickAppSelector
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.card_application_header),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (patchesLoaded) {
                            if (selectedAppPackage.isPresent) {
                                selectedAppPackage.get().packageName
                            } else {
                                stringResource(R.string.card_application_not_selected)
                            }
                        } else {
                            stringResource(R.string.card_application_not_loaded)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            ElevatedCard(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                enabled = hasAppSelected,
                onClick = onClickPatchSelector
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.card_patches_header),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (!hasAppSelected) {
                            "Select an application first."
                        } else if (viewModel.anyPatchSelected()) {
                            "$selectedAmount patches selected."
                        } else {
                            stringResource(R.string.card_patches_body_patches)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}