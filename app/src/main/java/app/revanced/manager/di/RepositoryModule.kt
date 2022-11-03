package app.revanced.manager.di

import app.revanced.manager.domain.repository.GithubRepositoryImpl
import app.revanced.manager.domain.repository.ReVancedRepositoryImpl
import app.revanced.manager.network.api.ManagerAPI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::GithubRepositoryImpl)
    singleOf(::ReVancedRepositoryImpl)
    singleOf(::ManagerAPI)
}