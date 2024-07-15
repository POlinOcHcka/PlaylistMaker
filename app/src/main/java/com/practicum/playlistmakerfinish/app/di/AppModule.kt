package com.practicum.playlistmakerfinish.app.di

import com.practicum.playlistmakerfinish.app.data.ThemeRepositoryImpl
import com.practicum.playlistmakerfinish.app.domain.GetThemeUseCase
import com.practicum.playlistmakerfinish.app.domain.SetThemeUseCase
import com.practicum.playlistmakerfinish.app.domain.ThemeRepository
import com.practicum.playlistmakerfinish.app.presentation.AppViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    factory { GetThemeUseCase(get()) }
    factory { SetThemeUseCase(get()) }
    viewModel { AppViewModel(get(), get()) }
}