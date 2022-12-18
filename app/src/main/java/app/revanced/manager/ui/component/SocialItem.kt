package app.revanced.manager.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import java.security.InvalidParameterException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialItem(
    label: String,
    icon: Any,
    onClick: () -> Unit
) {
    when (icon) {
        is ImageVector -> {
            ListItem(
                modifier = Modifier.clickable { onClick() },
                leadingContent = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                headlineText = { Text(text = label) }
            )
        }
        is Painter -> {
            ListItem(
                modifier = Modifier.clickable { onClick() },
                leadingContent = {
                        Icon(
                            painter = icon,
                            contentDescription = label
                        )
                    },
                headlineText = { Text(label) }
            )
        }
        else -> {
            throw InvalidParameterException()
        }
    }
}