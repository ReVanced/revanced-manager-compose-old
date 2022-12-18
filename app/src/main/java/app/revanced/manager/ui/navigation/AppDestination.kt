package app.revanced.manager.ui.navigation

import android.app.Activity
import android.os.Parcelable
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
import app.revanced.manager.ui.viewmodel.PatchedApp
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.replaceAll
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
sealed interface AppDestination : Parcelable {

    @Parcelize
    object Dashboard : DashboardDestination(
        Icons.Default.Dashboard, Icons.Outlined.Dashboard, R.string.dashboard
    )

    @Parcelize
    object Patcher : DashboardDestination(
        Icons.Default.Build, Icons.Outlined.Build, R.string.patcher
    )

    @Parcelize
    object Settings : DashboardDestination(
        Icons.Default.Settings, Icons.Outlined.Settings, R.string.settings
    )

    @Parcelize
    object AppSelector : AppDestination

    @Parcelize
    object PatchSelector : AppDestination

    @Parcelize
    object Patching : AppDestination

    @Parcelize
    class AppInfo(val patchedApp: PatchedApp) : AppDestination

    @Parcelize
    object SourceSelector : AppDestination

    @Parcelize
    object Licenses : AppDestination

    @Parcelize
    object Contributors : AppDestination
}

@Parcelize
sealed class DashboardDestination(
    val icon: @RawValue ImageVector,
    val outlinedIcon: @RawValue ImageVector,
    @StringRes val label: Int
) : AppDestination

/**
 * @author Aliucord Authors, DiamondMiner88
 */
context(Activity)
fun NavController<AppDestination>.back() {
    val topDest = backstack.entries.lastOrNull()?.destination

    if (topDest == AppDestination.Dashboard) {
        finish()
    } else if (topDest is DashboardDestination) {
        replaceAll(AppDestination.Dashboard)
    } else if (backstack.entries.size > 1) {
        pop()
    } else {
        replaceAll(AppDestination.Dashboard)
    }
}