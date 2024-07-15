package com.practicum.playlistmakerfinish.settings.di

import com.practicum.playlistmakerfinish.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmakerfinish.settings.domain.SettingsRepository
import com.practicum.playlistmakerfinish.settings.domain.SwitchThemeUseCase
import com.practicum.playlistmakerfinish.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    factory { SwitchThemeUseCase(get()) }
    viewModel { SettingsViewModel(get()) }
}