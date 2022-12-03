package app.revanced.manager.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.navigation.DashboardDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { topBar(scrollBehavior) },
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLargeTopBar(
    topBarTitle: String,
    scrollBehavior: TopAppBarScrollBehavior,
    actions: @Composable (RowScope.() -> Unit) = {},
    onBackClick: (() -> Unit)? = null
) {
    LargeTopAppBar(
        title = { Text(topBarTitle) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMediumTopBar(
    topBarTitle: String,
    scrollBehavior: TopAppBarScrollBehavior,
    actions: @Composable (RowScope.() -> Unit) = {},
    onBackClick: (() -> Unit)? = null
) {
    MediumTopAppBar(
        title = { Text(topBarTitle) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        actions = actions
    )
}

@Composable
fun AppBottomNavBar(
    navItems: List<DashboardDestination>,
    currentDestination: DashboardDestination,
    onNavChanged: (AppDestination) -> Unit
) {
    NavigationBar {
        navItems.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination,
                icon = {
                    Icon(
                        if (currentDestination == destination) destination.icon else destination.outlinedIcon,
                        stringResource(destination.label)
                    )
                },
                label = { Text(stringResource(destination.label)) },
                onClick = {
                    if (destination != currentDestination) {
                        onNavChanged(destination)
                    }
                }
            )
        }
    }
}