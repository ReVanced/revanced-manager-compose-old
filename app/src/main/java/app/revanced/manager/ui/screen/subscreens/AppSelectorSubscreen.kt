package app.revanced.manager.ui.screen.subscreens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.revanced.manager.R
import app.revanced.manager.ui.component.AppIcon
import app.revanced.manager.ui.component.LoadingIndicator
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.viewmodel.AppSelectorViewModel
import com.xinto.taxi.BackstackNavigator
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("QueryPermissionsNeeded", "UnrememberedMutableState")
@Composable
fun AppSelectorSubscreen(
    navigator: BackstackNavigator<AppDestination>,
    vm: AppSelectorViewModel = getViewModel(),
) {
    val context = LocalContext.current

    val filtered = mutableStateOf(vm.filteredApps.isNotEmpty())

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let { uri ->
            vm.setSelectedAppPackageFromFile(uri)
            navigator.pop()
            return@rememberLauncherForActivityResult
        }
        Toast.makeText(context, "Couldn't load APK file.", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(title = { Text(stringResource(R.string.app_selector_title)) },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    filePicker.launch(arrayOf("application/vnd.android.package-archive"))
                },
                icon = { Icon(Icons.Default.SdStorage, contentDescription = null) },
                text = { Text(stringResource(R.string.storage)) },
            )
        },
    ) { paddingValues ->
        if (filtered.value) {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(count = vm.filteredApps.size) { int ->
                    val app = vm.filteredApps[int]
                    val label = vm.applicationLabel(app)
                    val packageName = app.packageName

                    val same = packageName == label
                    ListItem(modifier = Modifier.clickable {
                        vm.setSelectedAppPackage(app)
                        navigator.pop()
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