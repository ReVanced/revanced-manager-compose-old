package app.revanced.manager.di

import app.revanced.manager.network.service.*
import io.ktor.client.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {

    fun provideGithubService(
        client: HttpService,
    ): GithubService {
        return GithubServiceImpl(
            client = client,
        )
    }

    fun provideReVancedService(
        client: HttpService,
    ): ReVancedService {
        return ReVancedServiceImpl(
            client = client,
        )
    }

    single { provideGithubService(get()) }
    single { provideReVancedService(get()) }
    singleOf(::HttpService)
}