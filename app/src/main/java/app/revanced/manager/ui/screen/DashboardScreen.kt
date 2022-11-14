package app.revanced.manager.ui.screen

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.ui.component.AppIcon
import app.revanced.manager.ui.component.ApplicationItem
import app.revanced.manager.ui.component.HeadlineWithCard
import app.revanced.manager.ui.viewmodel.DashboardViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = getViewModel()) {
    val context = LocalContext.current
    val padHoriz = 16.dp
    val padVert = 10.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        HeadlineWithCard(R.string.updates) {
            Row(
                modifier = Modifier
                    .padding(horizontal = padHoriz, vertical = padVert)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    CommitDate(
                        label = R.string.patcher,
                        date = viewModel.patcherCommitDate
                    )
                    CommitDate(
                        label = R.string.manager,
                        date = viewModel.managerCommitDate
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        enabled = false, // needs update
                        onClick = {
                            Toast.makeText(context, "Already up-to-date!", Toast.LENGTH_SHORT)
                                .show()
                        },
                    ) { Text(stringResource(R.string.update_patch_bundle)) }

                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.patched_applications),
                style = MaterialTheme.typography.headlineSmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = true, onClick = { /*TODO*/ }, label = {
                    Text(stringResource(R.string.updates_available))
                })
                FilterChip(selected = false, onClick = { /*TODO*/ }, label = {
                    Text(stringResource(R.string.installed))
                })
            }

            Column(
                modifier = Modifier
                    .padding(bottom = padVert)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ApplicationItem(
                    appName = "Compose Manager",
                    appIcon = {
                        AppIcon(drawable = LocalContext.current.packageManager.getApplicationIcon("app.revanced.manager.compose"), contentDescription = null, size = 38)
                    },
                    releaseAgo = "9d ago"
                ) {
                    ChangelogText(
                        """
                            cossal will explode
                        """.trimIndent()
                    )
                }
                ApplicationItem(
                    appName = "Flutter Manager",
                    releaseAgo = "9d ago",
                    appIcon = { AppIcon(drawable = LocalContext.current.packageManager.getApplicationIcon("app.revanced.manager.flutter"), contentDescription = null, size = 38) }
                ) {
                    ChangelogText(
                        """
                            cossal will explode
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

@Composable
fun CommitDate(@StringRes label: Int, date: String) {
    Row {
        Text(
            text = "${stringResource(label)}: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ChangelogText(text: String) {
    Column {
        Text(
            text = stringResource(R.string.changelog),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}