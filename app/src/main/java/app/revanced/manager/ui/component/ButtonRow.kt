package app.revanced.manager.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Launch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ButtonRow(
    buttons: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(16.dp, 16.dp, 16.dp, 0.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            buttons()
        }
    }
}

@Composable
fun RowScope.ButtonRowItem(
    icon: ImageVector,
    @StringRes label: Int,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                if (onClick != null) {
                    onClick()
                }
            }
            .fillMaxSize()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(id = label),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}