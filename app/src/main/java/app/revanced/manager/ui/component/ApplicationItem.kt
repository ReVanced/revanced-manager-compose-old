package app.revanced.manager.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.revanced.manager.R

@Composable
fun ApplicationItem(
    appName: String,
    appIcon: @Composable () -> Unit,
    releaseAgo: String,
    expandedContent: @Composable () -> Unit,
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotateState by animateFloatAsState(targetValue = if (expandedState) 180f else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 68.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.5f
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier
                        .height(68.dp)
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    appIcon()
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = appName,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Text(
                            text = releaseAgo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        modifier = Modifier.rotate(rotateState),
                        onClick = { expandedState = !expandedState },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = stringResource(R.string.expand)
                        )
                    }
                    OutlinedButton(onClick = { /*TODO*/ }) {
                        Text(stringResource(R.string.update))
                    }
                }
            }
            AnimatedVisibility(
                visible = expandedState
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxSize(),
                ) {
                    expandedContent()
                }
            }
        }
    }
}

@Composable
fun ApplicationItemDualTint(
    appName: String,
    appIcon: @Composable () -> Unit,
    releaseAgo: String,
    expandedContent: @Composable () -> Unit,
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotateState by animateFloatAsState(targetValue = if (expandedState) 180f else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 68.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E2630)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier
                            .height(68.dp)
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        appIcon()
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = appName,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Text(
                                text = releaseAgo,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            modifier = Modifier.rotate(rotateState),
                            onClick = { expandedState = !expandedState },
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = stringResource(R.string.expand)
                            )
                        }
                        OutlinedButton(onClick = { /*TODO*/ }) {
                            Text(stringResource(R.string.update))
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = expandedState
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0xFF11161C))
                        .padding(14.dp, 10.dp),
                ) {
                    expandedContent()
                }
            }
        }
    }
}