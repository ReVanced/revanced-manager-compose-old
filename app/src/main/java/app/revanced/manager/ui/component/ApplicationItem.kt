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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.revanced.manager.R

@Composable
fun ApplicationItem(
    appName: String,
    appIcon: @Composable () -> Unit,
    appVersion: String,
    onClick: () -> Unit,
    expandedContent: (@Composable () -> Unit)? = null,
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
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .height(68.dp)
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    appIcon()
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = appName,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Text(
                            text = appVersion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Row {
                    if (expandedContent != null) {
                        IconButton(
                            modifier = Modifier.rotate(rotateState),
                            onClick = { expandedState = !expandedState },
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = stringResource(R.string.expand)
                            )
                        }
                    }
                    OutlinedButton(onClick = onClick) {
                        Text(stringResource(R.string.info))
                    }
                }
            }
            if (expandedContent != null) {
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
}

@Composable
fun ApplicationItemDualTint(
    appName: String,
    appIcon: @Composable () -> Unit,
    appVersion: String,
    onClick: () -> Unit,
    expandedContent: (@Composable () -> Unit)? = null,
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotateState by animateFloatAsState(targetValue = if (expandedState) 180f else 0f)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 68.dp)
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        appIcon()
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = appName,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Text(
                                text = appVersion,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (expandedContent != null) {
                            IconButton(
                                modifier = Modifier.rotate(rotateState),
                                onClick = { expandedState = !expandedState },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = stringResource(R.string.expand)
                                )
                            }
                        }
                        Button(onClick = onClick) {
                            Text(stringResource(R.string.info))
                        }
                    }
                }
            }
            if (expandedContent != null) {
                AnimatedVisibility(
                    visible = expandedState
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.surface.copy(alpha = .4f).compositeOver(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)
                            ))
                            .padding(14.dp, 10.dp),
                    ) {
                        expandedContent()
                    }
                }
            }
        }
    }
}