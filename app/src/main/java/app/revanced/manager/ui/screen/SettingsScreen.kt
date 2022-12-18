package app.revanced.manager.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.revanced.manager.R
import app.revanced.manager.domain.manager.PreferencesManager
import app.revanced.manager.ui.component.GroupHeader
import app.revanced.manager.ui.component.SocialItem
import app.revanced.manager.ui.theme.Theme
import app.revanced.manager.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@SuppressLint("BatteryLife")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: SettingsViewModel = getViewModel(),
    onClickContributors: () -> Unit,
    onClickLicenses: () -> Unit,
) {
    val prefs = vm.prefs
    val context = LocalContext.current
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    var showBatteryButton by remember { mutableStateOf(!pm.isIgnoringBatteryOptimizations(context.packageName)) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 48.dp, start = 18.dp, end = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (vm.showThemePicker) {
            ThemePicker(
                onDismissRequest = vm::dismissThemePicker, onConfirm = vm::setTheme
            )
        }
        AnimatedVisibility(visible = showBatteryButton) {
            Card(
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    })
                    showBatteryButton = !pm.isIgnoringBatteryOptimizations(context.packageName)
                }, shape = MaterialTheme.shapes.extraLarge
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                ) {
                    Icon(
                        Icons.Default.BatteryChargingFull,
                        "Battery Optimization",
                        Modifier
                            .padding(start = 8.dp, end = 16.dp)
                            .size(24.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Battery Optimization",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        )
                        Text(
                            text = "Manager needs battery optimization to be disabled for working inbackground correctly",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
        GroupHeader(stringResource(R.string.appearance))
        ListItem(modifier = Modifier.clickable { vm.showThemePicker() },
            headlineText = { Text(stringResource(R.string.theme)) },
            leadingContent = { Icon(Icons.Default.Style, contentDescription = null) },
            trailingContent = {
                FilledTonalButton(onClick = { vm.showThemePicker() }) {
                    Text(text = prefs.theme.displayName)
                }
            })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ListItem(modifier = Modifier.clickable { prefs.dynamicColor = !prefs.dynamicColor },
                headlineText = { Text(stringResource(R.string.dynamic_color)) },
                leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                trailingContent = {
                    Switch(checked = prefs.dynamicColor,
                        onCheckedChange = { prefs.dynamicColor = it })
                })
        }
        ListItem(modifier = Modifier.clickable { prefs.sentry = !prefs.sentry },
            headlineText = { Text(stringResource(R.string.sentry)) },
            leadingContent = {
                Icon(
                    Icons.Default.IntegrationInstructions, contentDescription = null
                )
            },
            trailingContent = {
                Switch(checked = prefs.sentry, onCheckedChange = { prefs.sentry = it })
            })
        Divider()
        SocialItem(stringResource(id = R.string.github), R.drawable.ic_github, vm::openGitHub)
        SocialItem(stringResource(id = R.string.opensource_licenses), Icons.Default.LibraryBooks, onClickLicenses)
        SocialItem(stringResource(id = R.string.screen_contributors_title), Icons.Default.Group, onClickContributors)
    }
}

@Composable
fun ThemePicker(
    onDismissRequest: () -> Unit, onConfirm: (Theme) -> Unit, prefs: PreferencesManager = get()
) {
    var selectedTheme by remember { mutableStateOf(prefs.theme) }

    AlertDialog(onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.theme)) },
        text = {
            Column {
                Theme.values().forEach { theme ->
                    Row(
                        modifier = Modifier.clickable { selectedTheme = theme },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            theme.displayName, style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(Modifier.weight(1f, true))

                        RadioButton(selected = theme == selectedTheme,
                            onClick = { selectedTheme = theme })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(selectedTheme)
                onDismissRequest()
            }) {
                Text(stringResource(R.string.apply))
            }
        })
}