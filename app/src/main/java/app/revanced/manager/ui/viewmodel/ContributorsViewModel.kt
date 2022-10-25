package app.revanced.manager.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.network.api.ReVancedAPI
import app.revanced.manager.network.dto.revanced.Contributor
import app.revanced.manager.util.*
import kotlinx.coroutines.launch

class ContributorsViewModel(
    private val app: Application,
    private val reVancedAPI: ReVancedAPI
) : ViewModel() {
    val patcherContributorsList = mutableStateListOf<Contributor>()
    val patchesContributorsList = mutableStateListOf<Contributor>()
    val cliContributorsList = mutableStateListOf<Contributor>()
    val managerContributorsList = mutableStateListOf<Contributor>()
    val integrationsContributorsList = mutableStateListOf<Contributor>()

    private fun loadContributors() {
        viewModelScope.launch {
            val contributors = reVancedAPI.fetchContributors()
            contributors.repositories.forEach { repo ->
                when (repo.name) {
                    ghCli -> {
                        repo.contributors.sortedByDescending {
                            it.username
                        }
                        cliContributorsList.addAll(repo.contributors)
                    }
                    ghPatcher -> {
                        repo.contributors.sortedByDescending {
                            it.username
                        }
                        patcherContributorsList.addAll(repo.contributors)
                    }
                    ghPatches -> {
                        repo.contributors.sortedByDescending {
                            it.username
                        }
                        patchesContributorsList.addAll(repo.contributors)
                    }
                    ghIntegrations -> {
                        repo.contributors.sortedByDescending {
                            it.username
                        }
                        integrationsContributorsList.addAll(repo.contributors)
                    }
                    ghManager -> {
                        repo.contributors.sortedByDescending {
                            it.username
                        }
                        managerContributorsList.addAll(repo.contributors)
                    }
                }
            }
        }
    }

    fun openUserProfile(username: String) {
        app.openUrl("https://github.com/${username}")
    }

    init {
        viewModelScope.launch {
            loadContributors()
        }
    }
}