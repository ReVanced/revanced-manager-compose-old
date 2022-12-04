package app.revanced.manager.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.revanced.manager.ui.component.AppBottomNavBar
import app.revanced.manager.ui.component.AppLargeTopBar
import app.revanced.manager.ui.component.AppScaffold
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.navigation.DashboardDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    currentDestination: DashboardDestination,
    bottomNavItems: List<DashboardDestination>,
    onNavChanged: (AppDestination) -> Unit,
    content: @Composable () -> Unit
) {
    AppScaffold(
        topBar = { scrollBehavior ->
            AppLargeTopBar(
                topBarTitle = stringResource(currentDestination.label),
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AppBottomNavBar(
                navItems = bottomNavItems,
                currentDestination = currentDestination,
                onNavChanged = onNavChanged
            )
        }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues).fillMaxSize()) {
            content()
        }
    }
}