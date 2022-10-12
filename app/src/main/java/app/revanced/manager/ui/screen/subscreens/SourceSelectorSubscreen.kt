package app.revanced.manager.ui.screen.subscreens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.ui.component.SourceItem
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.viewmodel.PatcherScreenViewModel
import com.xinto.taxi.BackstackNavigator
import org.koin.androidx.compose.getViewModel
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceSelectorSubscreen(
    navigator: BackstackNavigator<AppDestination>,
    pvm: PatcherScreenViewModel = getViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true }
    )
    val context = LocalContext.current

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let { uri ->
            val patchesFile = context.cacheDir.resolve("patches.jar")
            Files.copy(
                context.contentResolver.openInputStream(uri),
                patchesFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
            pvm.patchBundleFile = patchesFile.absolutePath
            pvm.loadPatches0()
            navigator.pop()
            return@rememberLauncherForActivityResult
        }
        Toast.makeText(context, "Couldn't load local patch bundle.", Toast.LENGTH_SHORT).show()
    }


    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(R.string.select_sources)) },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO */ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ListItem(
                modifier = Modifier
                    .clickable { filePicker.launch(arrayOf("application/java-archive")) },
                headlineText = { Text(stringResource(R.string.select_bundle_from_storage)) },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.uwu),
                        contentDescription = null
                    )
                }
            )
            Divider()
            SourceItem()
            SourceItem()
            SourceItem()
        }
    }
}