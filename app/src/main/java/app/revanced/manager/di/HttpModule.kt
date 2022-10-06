package app.revanced.manager.di

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import okhttp3.Dns
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.net.Inet4Address
import java.net.InetAddress

val httpModule = module {
    fun provideHttpClient() = HttpClient(OkHttp) {
        engine {
            config {
                dns(object : Dns {
                    override fun lookup(hostname: String): List<InetAddress> {
                        val addresses = Dns.SYSTEM.lookup(hostname)
                        return addresses.filterIsInstance<Inet4Address>()
                    }
                })
            }
        }
        BrowserUserAgent()
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    singleOf(::provideHttpClient)
}