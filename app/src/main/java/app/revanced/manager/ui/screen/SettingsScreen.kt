package app.revanced.manager.ui.screen

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.preferences.PreferencesManager
import app.revanced.manager.ui.component.GroupHeader
import app.revanced.manager.ui.component.SocialItem
import app.revanced.manager.ui.theme.Theme
import app.revanced.manager.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = getViewModel()) {
    val prefs = viewModel.prefs

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (viewModel.showThemePicker) {
            ThemePicker(
                onDismissRequest = viewModel::dismissThemePicker,
                onConfirm = viewModel::setTheme
            )
        }
        GroupHeader(title = "Appearance")
        ListItem(
            modifier = Modifier.clickable { viewModel.showThemePicker() },
            headlineText = { Text(stringResource(R.string.theme)) },
            leadingContent = { Icon(Icons.Default.Style, contentDescription = null) },
            trailingContent = { FilledTonalButton(onClick = { viewModel.showThemePicker() }) {
                Text(text = prefs.theme.displayName)
            } }
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ListItem(
                modifier = Modifier.clickable { prefs.dynamicColor = !prefs.dynamicColor },
                headlineText = { Text(stringResource(R.string.dynamic_color)) },
                leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = prefs.dynamicColor,
                        onCheckedChange = { prefs.dynamicColor = it }
                    )
                }
            )
        }

        Divider()
        SocialItem(R.string.github, Icons.Default.Code, viewModel::openGitHub)
    }
}

@Composable
fun ThemePicker(
    onDismissRequest: () -> Unit,
    onConfirm: (Theme) -> Unit,
    prefs: PreferencesManager = get()
) {
    var selectedTheme by remember { mutableStateOf(prefs.theme) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.theme)) },
        text = {
            Column {
                Theme.values().forEach { theme ->
                    Row(
                        modifier = Modifier.clickable { selectedTheme = theme },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            theme.displayName,
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(Modifier.weight(1f, true))

                        RadioButton(
                            selected = theme == selectedTheme,
                            onClick = { selectedTheme = theme }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedTheme)
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.apply))
            }
        }
    )
}