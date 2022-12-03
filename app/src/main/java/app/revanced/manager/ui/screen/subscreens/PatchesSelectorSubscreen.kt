package app.revanced.manager.ui.screen.subscreens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.ui.component.AppMediumTopBar
import app.revanced.manager.ui.component.AppScaffold
import app.revanced.manager.ui.component.LoadingIndicator
import app.revanced.manager.ui.component.PatchCard
import app.revanced.manager.ui.viewmodel.PatchesSelectorViewModel
import app.revanced.patcher.extensions.PatchExtensions.patchName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchesSelectorSubscreen(
    onBackClick: () -> Unit,
    vm: PatchesSelectorViewModel = getViewModel(),
) {
    val patches = vm.filteredPatches
    var query by mutableStateOf("")

    LaunchedEffect(null) {
        launch(Dispatchers.Default) {
            vm.filterPatches()
        }
    }
    AppScaffold(
        topBar = { scrollBehavior ->
          AppMediumTopBar(
              topBarTitle = stringResource(id = R.string.card_patches_header),
              scrollBehavior = scrollBehavior,
              actions = {
                  IconButton(onClick = {
                      vm.selectAllPatches(patches, vm.selectedPatches.isEmpty())
                  }) {
                      if (vm.selectedPatches.isEmpty()) Icon(
                          Icons.Default.SelectAll, contentDescription = null
                      ) else Icon(Icons.Default.Deselect, contentDescription = null)
                  }
              },
              onBackClick = onBackClick
          )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            if (!vm.loading) {
                if (patches.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 4.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                shape = RoundedCornerShape(12.dp),
                                value = query,
                                onValueChange = { newValue ->
                                    query = newValue
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, "Search")
                                },
                                trailingIcon = {
                                    if (query.isNotEmpty()) {
                                        IconButton(onClick = {
                                            query = ""
                                        }) {
                                            Icon(Icons.Default.Clear, "Clear")
                                        }
                                    }
                                },
                            )
                        }
                    }
                    LazyColumn(Modifier.padding(0.dp, 2.dp)) {

                        if (query.isEmpty() || query.isBlank()) {
                            items(count = patches.size) {
                                val patch = patches[it]
                                val name = patch.patch.patchName
                                PatchCard(patch, vm.isPatchSelected(name)) {
                                    vm.selectPatch(name, !vm.isPatchSelected(name))
                                }
                            }
                        } else {
                            items(count = patches.size) {
                                val patch = patches[it]
                                val name = patch.patch.patchName
                                if (name.contains(query.lowercase())) {
                                    PatchCard(patch, vm.isPatchSelected(name)) {
                                        vm.selectPatch(name, !vm.isPatchSelected(name))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        Modifier.fillMaxSize(),
                        Arrangement.Center,
                        Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.no_compatible_patches))
                    }
                }
            } else LoadingIndicator(null)
        }
    }
}