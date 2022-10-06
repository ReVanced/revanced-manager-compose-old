package app.revanced.manager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.viewmodel.Logging
import app.revanced.manager.ui.viewmodel.PatchingScreenViewModel
import com.xinto.taxi.BackstackNavigator
import org.koin.androidx.compose.getViewModel


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PatchingScreen(
    navigator: BackstackNavigator<AppDestination>,
    vm: PatchingScreenViewModel = getViewModel()
) {
    Scaffold(
        topBar = {
            Row {
                IconButton(onClick = navigator::pop) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        }
    ) { paddingValues ->
        Column {
            Column(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (vm.patchingInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .size(30.dp)
                        )
                        Text(text = "Patching...", fontSize = 30.sp)
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Card {
                    Text(
                        text = Logging.log,
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .fillMaxSize(),
                        fontSize = 20.sp,
                        lineHeight = 35.sp
                    )
                }
            }
        }
    }
}