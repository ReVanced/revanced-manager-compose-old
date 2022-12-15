package app.revanced.manager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                enabled = vm.selectedAppPackage.isPresent && vm.selectedPatches.isNotEmpty(),
                onClick = onClickPatch,
                icon = { Icon(Icons.Default.Build, contentDescription = "Patch") },
                text = { Text(stringResource(R.string.patch)) }
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
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
                enabled = vm.patches is Resource.Success,
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
                            text = if (vm.patches is Resource.Success) {
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
                enabled = vm.selectedAppPackage.isPresent,
                onClick = onClickPatchSelector
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.card_patches_header),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (!vm.selectedAppPackage.isPresent) {
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