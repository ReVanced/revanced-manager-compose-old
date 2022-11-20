package app.revanced.manager.ui.component

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun AppIcon(
    drawable: Drawable?,
    contentDescription: String?,
    size: Int = 48
) {
    var image: Painter = rememberVectorPainter(Icons.Outlined.Android)
    var colorFilter: ColorFilter? = ColorFilter.tint(LocalContentColor.current)

    if (drawable != null) {
        image = rememberDrawablePainter(drawable)
        colorFilter = null
    }

    Image(
        image,
        contentDescription,
        Modifier.size(size.dp),
        colorFilter = colorFilter
    )
}