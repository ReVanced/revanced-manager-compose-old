package app.revanced.manager.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.domain.repository.ReVancedRepositoryImpl
import app.revanced.manager.network.dto.ReVancedContributor
import app.revanced.manager.network.utils.getOrNull
import app.revanced.manager.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContributorsViewModel(
    private val app: Application, private val reVancedAPI: ReVancedRepositoryImpl
) : ViewModel() {
    val patcherContributorsList = mutableStateListOf<ReVancedContributor>()
    val patchesContributorsList = mutableStateListOf<ReVancedContributor>()
    val cliContributorsList = mutableStateListOf<ReVancedContributor>()
    val managerContributorsList = mutableStateListOf<ReVancedContributor>()
    val integrationsContributorsList = mutableStateListOf<ReVancedContributor>()

    private suspend fun loadContributors() = withContext(Dispatchers.IO) {
        val contributors = reVancedAPI.getContributors().getOrNull() ?: return@withContext
        contributors.repositories.forEach { repo ->
            val list = when (repo.name) {
                ghCli -> cliContributorsList
                ghPatcher -> patcherContributorsList
                ghPatches -> patchesContributorsList
                ghIntegrations -> integrationsContributorsList
                ghManager -> managerContributorsList
                else -> return@forEach
            }
            withContext(Dispatchers.Main) {
                list.addAll(repo.contributors)
            }
        }
    }

    fun openUserProfile(username: String) {
        app.openUrl("https://github.com/$username")
    }

    init {
        viewModelScope.launch {
                loadContributors()
        }
    }
}