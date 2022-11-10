package app.revanced.manager.ui.component

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.network.dto.ReVancedContributor
import app.revanced.manager.ui.viewmodel.ContributorsViewModel
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
@ExperimentalMaterial3Api
fun ContributorsCard(
    title: String,
    data: SnapshotStateList<ReVancedContributor>,
    vm: ContributorsViewModel = getViewModel()
) {
    val context = LocalContext.current
    Column(
        Modifier
            .padding(16.dp, 8.dp, 16.dp, 4.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )

        ElevatedCard {
            if (data.isNotEmpty()) {
                FlowRow(
                    mainAxisSize = SizeMode.Expand,
                    modifier = Modifier.padding(8.dp)
                ) {
                    data.forEach { contributor ->
                        AsyncImage(
                            model = contributor.avatarUrl,
                            contentDescription = stringResource(id = R.string.contributor_image),
                            Modifier
                                .padding(4.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .combinedClickable(
                                    onClick = { vm.openUserProfile(contributor.username) },
                                    onLongClick = {
                                        Toast
                                            .makeText(
                                                context,
                                                contributor.username,
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                )
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                    content = { CircularProgressIndicator() }
                )
            }
        }
    }
}