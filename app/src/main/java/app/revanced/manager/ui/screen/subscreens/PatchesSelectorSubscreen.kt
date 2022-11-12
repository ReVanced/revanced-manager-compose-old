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
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.ui.Resource
import app.revanced.manager.ui.component.LoadingIndicator
import app.revanced.manager.ui.component.PatchCard
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.viewmodel.PatchesSelectorViewModel
import app.revanced.patcher.extensions.PatchExtensions.patchName
import com.xinto.taxi.BackstackNavigator
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchesSelectorSubscreen(
    navigator: BackstackNavigator<AppDestination>,
    psvm: PatchesSelectorViewModel = getViewModel(),
    patcherUtils: PatcherUtils = get()
) {
    val patchesState by patcherUtils.patches
    val patches = psvm.getFilteredPatches()
    var query by mutableStateOf("")

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.card_patches_header),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        psvm.selectAllPatches(patches, !psvm.anyPatchSelected())
                    }) {
                        if (!psvm.anyPatchSelected()) Icon(
                            Icons.Default.SelectAll,
                            contentDescription = null
                        ) else Icon(Icons.Default.Deselect, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            when (patchesState) {
                is Resource.Success -> {
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
                                    PatchCard(patch, psvm.isPatchSelected(name)) {
                                        psvm.selectPatch(name, !psvm.isPatchSelected(name))
                                    }
                                }
                            } else {
                                items(count = patches.size) {
                                    val patch = patches[it]
                                    val name = patch.patch.patchName
                                    if (name.contains(query.lowercase())) {
                                        PatchCard(patch, psvm.isPatchSelected(name)) {
                                            psvm.selectPatch(name, !psvm.isPatchSelected(name))
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
                            Text(text = "No compatible patches found.")
                        }
                    }
                }
                else -> LoadingIndicator(null)
            }
        }
    }
}