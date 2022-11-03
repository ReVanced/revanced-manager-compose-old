package app.revanced.manager.di

import app.revanced.manager.network.service.GithubService
import app.revanced.manager.network.service.GithubServiceImpl
import app.revanced.manager.network.service.ReVancedService
import app.revanced.manager.network.service.ReVancedServiceImpl
import com.vk.knet.core.Knet
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val serviceModule = module {

    fun provideGithubService(
        client: Knet,
        json: Json
    ): GithubService {
        return GithubServiceImpl(
            client = client,
            json = json
        )
    }

    fun provideReVancedService(
        client: Knet,
        json: Json
    ): ReVancedService {
        return ReVancedServiceImpl(
            client = client,
            json = json
        )
    }

    single { provideGithubService(get(), get()) }
    single { provideReVancedService(get(), get()) }
}