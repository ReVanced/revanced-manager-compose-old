package app.revanced.manager.ui.screen.subscreens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.ui.component.ContributorsCard
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.viewmodel.ContributorsViewModel
import com.xinto.taxi.BackstackNavigator
import org.koin.androidx.compose.getViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ContributorsSubscreen(
    navigator: BackstackNavigator<AppDestination>,
    vm: ContributorsViewModel = getViewModel()
) {
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.screen_contributors_title),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .height(1400.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ContributorsCard(
                stringResource(R.string.cli_contributors),
                data = vm.cliContributorsList,
                size = 100
            )
            ContributorsCard(
                stringResource(R.string.patcher_contributors),
                data = vm.patcherContributorsList,
                size = 100
            )
            ContributorsCard(
                stringResource(R.string.patches_contributors),
                data = vm.patchesContributorsList,
                size = 200
            )
            ContributorsCard(
                stringResource(R.string.manager_contributors),
                data = vm.managerContributorsList,
                size = 100
            )
            ContributorsCard(
                stringResource(R.string.integrations_contributors),
                data = vm.integrationsContributorsList,
                size = 200
            )
        }
    }
}
