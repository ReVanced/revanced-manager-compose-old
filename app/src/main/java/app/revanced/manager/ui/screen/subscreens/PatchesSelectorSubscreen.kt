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
import app.revanced.manager.ui.component.PatchCard
import app.revanced.manager.ui.viewmodel.PatchesSelectorViewModel
import app.revanced.patcher.extensions.PatchExtensions.patchName
import org.koin.androidx.compose.getViewModel

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchesSelectorSubscreen(
    onBackClick: () -> Unit,
    vm: PatchesSelectorViewModel = getViewModel(),
) {

    AppScaffold(topBar = { scrollBehavior ->
        AppMediumTopBar(
            topBarTitle = stringResource(id = R.string.card_patches_header),
            scrollBehavior = scrollBehavior,
            actions = {
                IconButton(onClick = {
                    vm.selectAllPatches(vm.patches, vm.selectedPatches.isEmpty())
                }) {
                    if (vm.selectedPatches.isEmpty()) Icon(
                        Icons.Default.SelectAll, contentDescription = null
                    ) else Icon(Icons.Default.Deselect, contentDescription = null)
                }
            },
            onBackClick = onBackClick
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            val search = vm.search
            if (vm.patches.isNotEmpty()) {
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
                            value = search,
                            onValueChange = { vm.search(it) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, "Search")
                            },
                            trailingIcon = {
                                if (search.isNotEmpty()) {
                                    IconButton(onClick = {
                                        vm.clearSearch()
                                    }) {
                                        Icon(Icons.Default.Clear, "Clear")
                                    }
                                }
                            },
                        )
                    }
                }
                LazyColumn(Modifier.padding(0.dp, 2.dp)) {
                    items(count = vm.patches.size) {
                        val patchClass = vm.patches[it]
                        val name = patchClass.patch.patchName
                        if (search.isBlank() && name.contains(search.lowercase())) {
                            PatchCard(patchClass, vm.isPatchSelected(patchClass.patch)) {
                                vm.selectPatch(
                                    patchClass.patch,
                                    !vm.isPatchSelected(patchClass.patch)
                                )
                            }
                        }
                    }
                }
            } else {
                Column(
                    Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.no_compatible_patches))
                }
            }
        }
    }
}