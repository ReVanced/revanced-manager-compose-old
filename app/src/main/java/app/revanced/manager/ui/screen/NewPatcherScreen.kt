package app.revanced.manager.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.revanced.manager.ui.viewmodel.PatcherViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPatcherScreen(
    onClickAppSelector: () -> Unit,
    onClickPatchSelector: () -> Unit,
    viewModel: PatcherViewModel = getViewModel()
) {
    var validBundle = false
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        ElevatedCard(
            onClick = onClickAppSelector,
            modifier = Modifier
                .padding(16.dp, 4.dp)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
        ) {
            Column(
                modifier = Modifier.padding(12.dp, 8.dp, 12.dp, 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Row(modifier = Modifier.padding(0.dp, 12.dp)) {
                    Icon(
                        Icons.Default.FolderZip,
                        "Patch Bundle",
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        if (!validBundle) {
                            Text(
                                text = "Select a patch bundle",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(text = "(not selected)", fontSize = 13.sp)
                        } else {
                            Text(
                                text = "Selected patch bundle",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = viewModel.getSelectedPackageInfo()!!.applicationInfo.name,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f, true))
                }
            }
        }
    }
}