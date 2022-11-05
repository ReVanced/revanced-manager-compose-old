package app.revanced.manager.di

import app.revanced.manager.patcher.PatcherUtils
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val patcherModule = module {
    singleOf(::PatcherUtils)
}