package app.revanced.manager.di

import app.revanced.manager.api.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::GitHubAPI)
    singleOf(::ReVancedAPI)
}