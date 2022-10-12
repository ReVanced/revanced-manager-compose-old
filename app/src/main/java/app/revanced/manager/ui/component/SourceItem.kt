package app.revanced.manager.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SourceItem() {
    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable { }
    ) {
        AsyncImage(
            model = "https://github.com/ushie.png",
            contentDescription = stringResource(id = R.string.contributor_image),
            modifier = Modifier
                .padding(4.dp, 4.dp, 10.dp, 4.dp)
                .size(50.dp)
                .clip(RoundedCornerShape(20))
        )
        Column {
            Row(Modifier.padding(vertical = 5.dp)) {
                Text(
                    text = "Ushie",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "2.52.5",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(R.drawable.ic_github),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Ushie/revanced-patches",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Box(
            Modifier
                .align(Alignment.CenterVertically)
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}