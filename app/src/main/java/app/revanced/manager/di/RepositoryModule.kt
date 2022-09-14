package app.revanced.manager.di

import app.revanced.manager.api.API
import app.revanced.manager.repository.GitHubRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::GitHubRepository)
    singleOf(::API)
}