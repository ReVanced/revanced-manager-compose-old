package app.revanced.manager.ui.screen.subscreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.ui.component.AppLargeTopBar
import app.revanced.manager.ui.component.AppScaffold
import app.revanced.manager.ui.component.ContributorsCard
import app.revanced.manager.ui.viewmodel.ContributorsViewModel
import app.revanced.manager.util.ghOrganization
import app.revanced.manager.util.openUrl
import org.koin.androidx.compose.getViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ContributorsSubscreen(
    onBackClick: () -> Unit,
    vm: ContributorsViewModel = getViewModel()
) {
    val ctx = LocalContext.current.applicationContext
    AppScaffold(
        topBar = { scrollBehavior ->
          AppLargeTopBar(
              topBarTitle = stringResource(R.string.screen_contributors_title),
              scrollBehavior = scrollBehavior,
              onBackClick = onBackClick
          )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { ctx.openUrl(ghOrganization) }) {
                Icon(painterResource(id = R.drawable.ic_github), contentDescription = null)
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 8.dp)
        ) {
            ContributorsCard(
                stringResource(R.string.cli_contributors),
                data = vm.cliContributorsList
            )
            ContributorsCard(
                stringResource(R.string.patcher_contributors),
                data = vm.patcherContributorsList
            )
            ContributorsCard(
                stringResource(R.string.patches_contributors),
                data = vm.patchesContributorsList
            )
            ContributorsCard(
                stringResource(R.string.manager_contributors),
                data = vm.managerContributorsList
            )
            ContributorsCard(
                stringResource(R.string.integrations_contributors),
                data = vm.integrationsContributorsList
            )
        }
    }
}