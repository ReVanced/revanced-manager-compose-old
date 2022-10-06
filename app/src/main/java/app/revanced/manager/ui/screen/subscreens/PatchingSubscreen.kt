package app.revanced.manager.ui.screen.subscreens

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.viewmodel.Logging
import app.revanced.manager.ui.viewmodel.PatchingScreenViewModel
import com.xinto.taxi.BackstackNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@SuppressLint("UnrememberedMutableState")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PatchingSubscreen(
    navigator: BackstackNavigator<AppDestination>,
    vm: PatchingScreenViewModel = getViewModel()
) {
    var patching by mutableStateOf(false)
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
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
                        is PatchingScreenViewModel.Status.Failure -> {
                            Icon(
                                Icons.Default.Close,
                                "failed",
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(30.dp)
                            )
                            Text(text = "Failed!", fontSize = 30.sp)
                        }
                        is PatchingScreenViewModel.Status.Patching -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(30.dp)
                            )
                            Text(text = "Patching...", fontSize = 30.sp)
                        }
                        is PatchingScreenViewModel.Status.Success -> {
                            Icon(
                                Icons.Default.Done,
                                "done",
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(30.dp)
                            )
                            Text(text = "Completed!", fontSize = 30.sp)
                        }
                        PatchingScreenViewModel.Status.Idle -> {}
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                ElevatedCard {
                    Text(
                        text = Logging.log,
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        fontSize = 20.sp,
                        lineHeight = 35.sp,
                        overflow = TextOverflow.Visible,
                        onTextLayout = {
                            coroutineScope.launch {
                                scrollState.animateScrollTo(it.size.height, tween(1000, easing = LinearEasing))
                            }
                        }
                    )
                }
            }
        }
    }
}