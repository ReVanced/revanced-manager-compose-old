package app.revanced.manager.di

import app.revanced.manager.patcher.worker.PatcherWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    worker { PatcherWorker(androidContext(), get(), get(), get()) }
}