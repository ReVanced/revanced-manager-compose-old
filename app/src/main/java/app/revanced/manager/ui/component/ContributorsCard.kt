package app.revanced.manager.ui.component

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.dto.github.Contributor
import coil.compose.AsyncImage

@Composable
@ExperimentalMaterial3Api
fun ContributorsCard(
    title: String,
    size: Int,
    data: SnapshotStateList<Contributor>,
) {
    val context = LocalContext.current
    Column(
        Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge
        )

        if (data.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(48.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.height(size.dp),
                userScrollEnabled = false
            ) {
                items(data) { contributor ->
                    AsyncImage(
                        model = contributor.avatarUrl,
                        contentDescription = stringResource(id = R.string.contributor_image),
                        Modifier
                            .padding(4.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                Toast
                                    .makeText(context, contributor.username, Toast.LENGTH_SHORT)
                                    .show()
                            }
                    )
                }
            }
        }
    }
}