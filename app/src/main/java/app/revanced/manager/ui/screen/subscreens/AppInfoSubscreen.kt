package app.revanced.manager.ui.screen.subscreens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.revanced.manager.R
import app.revanced.manager.ui.component.AppIcon
import app.revanced.manager.ui.component.AppMediumTopBar
import app.revanced.manager.ui.component.AppScaffold
import app.revanced.manager.ui.viewmodel.PatchedApp
import app.revanced.manager.util.loadIcon
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoSubscreen(
    onBackClick: () -> Unit,
    patchedApp: PatchedApp,
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    AppScaffold(
        topBar = { scrollBehavior ->
            AppMediumTopBar(
                topBarTitle = stringResource(R.string.app_info),
                scrollBehavior = scrollBehavior,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            if (showDialog) {
                AlertDialog(
                    title = { Text(text = "Applied patches") },
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text(stringResource(R.string.dismiss))
                        }
                    },
                    text = {
                        LazyColumn {
                            items(count = patchedApp.appliedPatches.count()) {
                                Text(text = "\u2022 ${patchedApp.appliedPatches[it]}")
                            }
                        }
                    }
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AppIcon(
                    drawable = context.loadIcon(patchedApp.pkgName),
                    contentDescription = null,
                    size = 64
                )
                Text(
                    text = patchedApp.appName,
                    fontSize = 23.sp
                )
                Text(patchedApp.appVersion)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(16.dp, 16.dp, 16.dp, 0.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { }
                                .fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Launch,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Launch",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Divider(
                            modifier = Modifier
                                .width(.5.dp)
                                .fillMaxHeight()
                                .padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.background
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { }
                                .fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Uninstall",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Divider(
                            modifier = Modifier
                                .width(.5.dp)
                                .fillMaxHeight()
                                .padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.background
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { }
                                .fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Build,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Patch",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                ListItem(
                    headlineText = {
                        Text(
                            "Package name",
                            fontSize = 20.sp,
                            fontWeight = FontWeight(500)
                        )
                    },
                    supportingText = {
                        Text(
                            text = patchedApp.pkgName
                        )
                    }
                )
                ListItem(
                    headlineText = {
                        Text(
                            text = "Patched date",
                            fontSize = 20.sp,
                            fontWeight = FontWeight(500)
                        )
                    },
                    supportingText = {
                        Text(
                            text = patchedApp.patchedDate
                        )
                    }
                )
                ListItem(
                    modifier = Modifier.clickable { showDialog = true },
                    headlineText = {
                        Text(
                            text = "Applied patches",
                            fontSize = 20.sp,
                            fontWeight = FontWeight(500)
                        )
                    },
                    supportingText = {
                        Text(
                            text = if (patchedApp.appliedPatches.count() == 1) {
                                "1 applied patch"
                            } else {
                                "${patchedApp.appliedPatches.count()} applied patches"
                            }
                        )
                    }
                )
            }
        }
    }
}