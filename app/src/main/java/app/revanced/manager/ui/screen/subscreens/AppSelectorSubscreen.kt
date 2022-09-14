package app.revanced.manager.ui.screen.subscreens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.revanced.manager.R
import app.revanced.manager.Variables.filteredApps
import app.revanced.manager.Variables.patchesState
import app.revanced.manager.ui.Resource
import app.revanced.manager.ui.component.AppIcon
import app.revanced.manager.ui.component.LoadingIndicator
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.viewmodel.AppSelectorViewModel
import com.xinto.taxi.BackstackNavigator
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("QueryPermissionsNeeded")
@Composable
fun AppSelectorSubscreen(
    navigator: BackstackNavigator<AppDestination>,
    vm: AppSelectorViewModel = getViewModel(),
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.app_selector_title)) },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        when (patchesState) {
            is Resource.Success -> {
                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    items(count = filteredApps.size) {
                        val app = filteredApps[it]
                        val label = vm.applicationLabel(app)
                        val packageName = app.packageName

                        val same = packageName == label
                        ListItem(modifier = Modifier.clickable {
                            vm.setSelectedAppPackage(app.packageName)
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
            }
            else -> LoadingIndicator(null)
        }
    }
}