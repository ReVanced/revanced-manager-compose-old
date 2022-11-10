package app.revanced.manager

import android.app.Application
import app.revanced.manager.di.*
import coil.ImageLoader
import coil.ImageLoaderFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class ManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ManagerApplication)
            workManagerFactory()
            modules(
                httpModule,
                preferencesModule,
                viewModelModule,
                repositoryModule,
                workerModule,
                patcherModule,
                serviceModule
            )
        }
    }
}