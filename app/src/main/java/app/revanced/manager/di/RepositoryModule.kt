package app.revanced.manager.di

import app.revanced.manager.api.GitHubAPI
import app.revanced.manager.api.ReVancedAPI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::GitHubAPI)
    singleOf(::ReVancedAPI)
}