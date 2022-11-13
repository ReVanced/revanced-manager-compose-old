package app.revanced.manager.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import app.revanced.manager.R
import com.xinto.taxi.Destination
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
sealed interface AppDestination : Destination {
    @Parcelize
    object Dashboard : AppDestination

    @Parcelize
    object AppSelector : AppDestination

    @Parcelize
    object PatchSelector : AppDestination

    @Parcelize
    object Patcher : AppDestination

    @Parcelize
    object SourceSelector : AppDestination

    @Parcelize
    object Licenses : AppDestination

    @Parcelize
    object Contributors : AppDestination
}

@Parcelize
enum class DashboardDestination(
    val icon: @RawValue ImageVector,
    val outlinedIcon: @RawValue ImageVector,
    @StringRes val label: Int
) : Destination {
    DASHBOARD(Icons.Default.Dashboard, Icons.Outlined.Dashboard, R.string.dashboard),
    PATCHER(Icons.Default.Build, Icons.Outlined.Build, R.string.patcher),
    SETTINGS(Icons.Default.Settings, Icons.Outlined.Settings, R.string.settings)
}