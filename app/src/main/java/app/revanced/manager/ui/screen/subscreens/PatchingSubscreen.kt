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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.revanced.manager.R
import app.revanced.manager.ui.component.InstallFailureDialog
import app.revanced.manager.ui.viewmodel.PatchingScreenViewModel
import app.revanced.manager.ui.viewmodel.PatchingScreenViewModel.PatchLog
import app.revanced.manager.ui.viewmodel.PatchingScreenViewModel.Status
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@SuppressLint("UnrememberedMutableState")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PatchingSubscreen(
    onBackClick: () -> Unit,
    vm: PatchingScreenViewModel = getViewModel()

) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        }
    ) { paddingValues ->
        Column {
            if (vm.installFailure) {
                InstallFailureDialog(
                    onDismiss = { vm.installFailure = false },
                    status = vm.pmStatus,
                    result = vm.extra
                )
            }
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
                            Text(text = stringResource(R.string.failed), fontSize = 30.sp)
                        }
                        is Status.Patching -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(30.dp)
                            )
                            Text(text = stringResource(R.string.patching), fontSize = 30.sp)
                        }
                        is Status.Success -> {
                            Icon(
                                Icons.Default.Done,
                                "done",
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(30.dp)
                            )
                            Text(text = stringResource(R.string.completed), fontSize = 30.sp)
                        }
                        Status.Idle -> {}
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(20.dp)
            ) {
                ElevatedCard(
                    Modifier
                        .weight(1f, true)
                        .fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        vm.logs.forEach { log ->
                            Text(
                                modifier = Modifier.requiredHeightIn(min = 36.dp),
                                text = log.message,
                                color = when (log) {
                                    is PatchLog.Success -> Color.Green
                                    is PatchLog.Info -> LocalContentColor.current
                                    is PatchLog.Error -> Color.Red
                                },
                                fontSize = 20.sp,
                                onTextLayout = {
                                    coroutineScope.launch {
                                        scrollState.animateScrollTo(
                                            9999, tween(1000, easing = LinearEasing)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                if (vm.status is Status.Success) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Spacer(Modifier.weight(1f, true))
                        Button(onClick = {
                            vm.installApk(vm.outputFile)
                        }) {
                            Text(text = stringResource(R.string.install))
                        }
                    }
                }
            }
        }
    }
}