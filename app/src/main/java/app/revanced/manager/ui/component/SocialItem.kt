package app.revanced.manager.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialItem(@StringRes label: Int, imageVector: ImageVector? = null, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        leadingContent = {
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = stringResource(label)
                )
            }
        },
        headlineText = { Text(stringResource(label)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialItem(
    @StringRes label: Int,
    @DrawableRes painterResource: Int? = null,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        leadingContent = {
            if (painterResource != null) {
                Icon(
                    painter = painterResource(painterResource),
                    contentDescription = stringResource(label)
                )
            }
        },
        headlineText = { Text(stringResource(label)) }
    )
}