package app.revanced.manager.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.ui.theme.Typography
import app.revanced.manager.ui.viewmodel.PatchClass
import app.revanced.patcher.extensions.PatchExtensions.description
import app.revanced.patcher.extensions.PatchExtensions.options
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.extensions.PatchExtensions.version

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchCard(patchClass: PatchClass, isSelected: Boolean, onSelected: () -> Unit) {
    val patch = patchClass.patch
    val name = patch.patchName

    var showDialog by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .padding(16.dp, 4.dp),
        enabled = !patchClass.unsupported,
        onClick = onSelected
    ) {
        Column(modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name.replace("-", " ").split(" ")
                        .joinToString(" ") { it.replaceFirstChar(Char::uppercase) },
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = patch.version ?: "unknown",
                    style = Typography.bodySmall
                )
                Spacer(Modifier.weight(1f, true))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    if (patchClass.patch.options != null) {
                        CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                            IconButton(onClick = { }, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    Icons.Outlined.Settings,
                                    contentDescription = "Patch Options"
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                        Checkbox(
                            enabled = !patchClass.unsupported,
                                checked = isSelected,
                                onCheckedChange = { onSelected() }
                            )
                        }
                }
            }
            var isExpanded by remember { mutableStateOf(false) }
            patch.description?.let { desc ->
                Text(
                    text = desc,
                    modifier = Modifier
                        .padding(0.dp, 4.dp, 30.dp, 4.dp)
                        .clickable { isExpanded = !isExpanded },
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = true,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (patchClass.unsupported) {
                CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                    Column {
                        Row {
                            if (showDialog) {
                                PatchCompatibilityDialog(
                                    onClose = { showDialog = false },
                                    patchClass = patchClass,
                                )
                            }
                            InputChip(
                                selected = false,
                                onClick = { showDialog = true },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Warning,
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = stringResource(id = R.string.unsupported_version)
                                    )
                                },
                                label = { Text(stringResource(id = R.string.unsupported_version)) }
                            )
                        }
                    }
                }
            }
        }
    }
}