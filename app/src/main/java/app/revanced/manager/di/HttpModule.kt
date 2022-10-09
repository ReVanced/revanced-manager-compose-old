package app.revanced.manager.di

import android.content.Context
import com.niusounds.ktor.client.engine.cronet.Cronet
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val httpModule = module {
    fun provideHttpClient(appContext: Context) = HttpClient(
        engine = Cronet.create {
            context = appContext
            config = {
                enableBrotli(true)
                enableQuic(true)
            }
        }
    ) {
        BrowserUserAgent()
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 5)
        }
        install(HttpCache)
    }

    single {
        provideHttpClient(androidContext())
    }
}