package com.practicum.playlistmakerfinish.library.di

import com.practicum.playlistmakerfinish.library.domain.GetPlaylistUseCase
import com.practicum.playlistmakerfinish.library.presentation.PlaylistsViewModel
import com.practicum.playlistmakerfinish.library.presentation.ViewPlaylistViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistsModule = module {
    viewModel {
        PlaylistsViewModel(get())
    }

    factory { GetPlaylistUseCase(get()) }
    viewModel {
        ViewPlaylistViewModel(androidApplication(), get(), get())
    }
}