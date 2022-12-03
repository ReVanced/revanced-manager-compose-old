package app.revanced.manager.ui.screen.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.revanced.manager.R
import app.revanced.manager.ui.component.AppLargeTopBar
import app.revanced.manager.ui.component.AppScaffold
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesSubscreen(
    onBackClick: () -> Unit,
) {
    AppScaffold(
        topBar = { scrollBehavior ->
            AppLargeTopBar(
                topBarTitle = stringResource(R.string.opensource_licenses),
                scrollBehavior = scrollBehavior,
                onBackClick = onBackClick
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LibrariesContainer(
                modifier = Modifier
                    .fillMaxSize(),
                colors = LibraryDefaults.libraryColors(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    badgeBackgroundColor = MaterialTheme.colorScheme.primary,
                    badgeContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    }
}