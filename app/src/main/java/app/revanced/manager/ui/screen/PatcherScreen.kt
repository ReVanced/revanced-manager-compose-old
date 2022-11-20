package app.revanced.manager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.ui.Resource
import app.revanced.manager.ui.component.AppIcon
import app.revanced.manager.ui.component.FloatingActionButton
import app.revanced.manager.ui.component.SplitAPKDialog
import app.revanced.manager.ui.viewmodel.PatcherScreenViewModel
import app.revanced.manager.util.loadIcon
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatcherScreen(
    onClickAppSelector: () -> Unit,
    onClickPatchSelector: () -> Unit,
    onClickPatch: () -> Unit,
    onClickSourceSelector: () -> Unit,
    vm: PatcherScreenViewModel = getViewModel(),
) {
    var showDialog by remember { mutableStateOf(false) }
    val hasAppSelected by mutableStateOf(vm.selectedAppPackage.isPresent)
    val patchesLoaded by mutableStateOf(vm.patchesLoaded is Resource.Success)
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                enabled = hasAppSelected && vm.selectedPatches.isNotEmpty(),
                onClick = onClickPatch,
                icon = { Icon(Icons.Default.Build, contentDescription = "Patch") },
                text = { Text(stringResource(R.string.patch)) }
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (vm.selectedAppPackage.isPresent) {
                            AppIcon(
                                context.loadIcon(vm.selectedAppPackage.get().packageName),
                                contentDescription = null, size = 18
                            )
                            Spacer(Modifier.width(5.dp))
                        }
                        Text(
                            text = if (patchesLoaded) {
                                if (vm.selectedAppPackage.isPresent) {
                                    vm.selectedAppPackage.get().packageName
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
                            stringResource(R.string.select_an_application_first)
                        } else if (vm.selectedPatches.isNotEmpty()) {
                            "${vm.selectedPatches.size} patches selected."
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