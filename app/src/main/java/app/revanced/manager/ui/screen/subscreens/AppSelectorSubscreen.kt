package app.revanced.manager.ui.screen.subscreens

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.revanced.manager.R
import app.revanced.manager.ui.component.AppIcon
import app.revanced.manager.ui.component.AppMediumTopBar
import app.revanced.manager.ui.component.AppScaffold
import app.revanced.manager.ui.component.LoadingIndicator
import app.revanced.manager.ui.viewmodel.AppSelectorViewModel
import app.revanced.manager.util.appName
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("QueryPermissionsNeeded", "UnrememberedMutableState")
@Composable
fun AppSelectorSubscreen(
    onBackClick: () -> Unit,
    vm: AppSelectorViewModel = getViewModel(),
) {
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let { uri ->
            vm.setSelectedAppPackageFromFile(uri)
            onBackClick()
        }
    }

    AppScaffold(
        topBar = { scrollBehavior ->
          AppMediumTopBar(
              topBarTitle = stringResource(R.string.app_selector_title),
              scrollBehavior = scrollBehavior,
              onBackClick = onBackClick
          )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    filePicker.launch(arrayOf("application/vnd.android.package-archive"))
                },
                icon = { Icon(Icons.Default.SdStorage, contentDescription = null) },
                text = { Text(stringResource(R.string.storage)) },
            )
        }
    ) { paddingValues ->
        if (vm.filteredApps.isNotEmpty()) {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                items(count = vm.filteredApps.size) { int ->
                    val app = vm.filteredApps[int]
                    val label = vm.app.appName(app)
                    val packageName = app.packageName

                    val same = packageName == label
                    ListItem(modifier = Modifier.clickable {
                        vm.setSelectedAppPackage(app)
                        onBackClick()
                    }, leadingContent = {
                        AppIcon(vm.loadIcon(app), packageName)
                    }, headlineText = {
                        if (same) {
                            Text(packageName)
                        } else {
                            Text(label)
                        }
                    }, supportingText = {
                        if (!same) {
                            Text(packageName)
                        }
                    })
                }
            }
        } else LoadingIndicator(null)
    }
}