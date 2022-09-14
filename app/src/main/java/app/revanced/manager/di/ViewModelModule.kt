package app.revanced.manager.di

import app.revanced.manager.ui.viewmodel.AppSelectorViewModel
import app.revanced.manager.ui.viewmodel.DashboardViewModel
import app.revanced.manager.ui.viewmodel.PatcherViewModel
import app.revanced.manager.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::SettingsViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::PatcherViewModel)
    viewModelOf(::AppSelectorViewModel)
}