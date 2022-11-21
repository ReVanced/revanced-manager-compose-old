package app.revanced.manager.ui.screen.subscreens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.viewmodel.PatchingScreenViewModel
import app.revanced.manager.ui.viewmodel.PatchingScreenViewModel.PatchLog
import app.revanced.manager.ui.viewmodel.PatchingScreenViewModel.Status
import com.xinto.taxi.BackstackNavigator
import org.koin.androidx.compose.getViewModel


@SuppressLint("UnrememberedMutableState")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PatchingSubscreen(
    navigator: BackstackNavigator<AppDestination>,
    vm: PatchingScreenViewModel = getViewModel()
) {
    var patching by mutableStateOf(false)
    LaunchedEffect(patching) {
        if (!patching) {
            patching = true
            vm.startPatcher()
        }
    }

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

                    when (vm.status) {
                        is Status.Failure -> {
                            Icon(
                                Icons.Default.Close,
                                "failed",
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(30.dp)
                            )
                            Text(text = "Failed!", fontSize = 30.sp)
                        }
                        is Status.Patching -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(30.dp)
                            )
                            Text(text = "Patching...", fontSize = 30.sp)
                        }
                        is Status.Success -> {
                            Icon(
                                Icons.Default.Done,
                                "done",
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(30.dp)
                            )
                            Text(text = "Completed!", fontSize = 30.sp)
                        }
                        Status.Idle -> {}
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                ElevatedCard {
                    LazyColumn(
                        Modifier
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .fillMaxSize()
                    ) {
                        items(vm.logs) { log ->
                            Text(
                                modifier = Modifier.height(36.dp),
                                text = log.message,
                                color = when (log) {
                                    is PatchLog.Success -> Color.Green
                                    is PatchLog.Info -> LocalContentColor.current
                                    is PatchLog.Error -> Color.Red
                                },
                                fontSize = 20.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}