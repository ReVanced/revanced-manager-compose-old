package app.revanced.manager.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
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
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(currentDestination.label),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination == destination,
                        icon = {
                            Icon(
                                if (currentDestination == destination) destination.icon else destination.outlinedIcon,
                                stringResource(destination.label)
                            )
                        },
                        label = { Text(stringResource(destination.label)) },
                        onClick = { onNavChanged(destination) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            content()
        }
    }
}