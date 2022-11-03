package app.revanced.manager.di

import android.content.Context
import com.vk.knet.core.Knet
import com.vk.knet.core.utils.ByteArrayPool
import com.vk.knet.cornet.CronetKnetEngine
import com.vk.knet.cornet.config.CronetCache
import com.vk.knet.cornet.config.CronetQuic
import com.vk.knet.cornet.pool.buffer.CronetNativeByteBufferPool
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val httpModule = module {
    fun provideKnet(appContext: Context) = CronetKnetEngine.Build(appContext) {
        client {
            setCache(CronetCache.Disk(appContext.filesDir, 1024 * 1024 * 10))

            enableHttp2(true)
            enableQuic(
                CronetQuic()
            )

            useBrotli(true)
            connectTimeout(15, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            readTimeout(15, TimeUnit.SECONDS)

            nativePool(CronetNativeByteBufferPool.DEFAULT)
            arrayPool(ByteArrayPool.DEFAULT)

            maxConcurrentRequests(50)
            maxConcurrentRequestsPerHost(10)

            followRedirects(true)
            followSslRedirects(true)

        }
    }

    fun json() = Json {
        encodeDefaults = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    single {
        provideKnet(androidContext())
    }
    single {
        json()
    }
    single {
        Knet.Build(get<CronetKnetEngine>())
    }
}