package com.practicum.playlistmakerfinish.library.di

import com.practicum.playlistmakerfinish.library.presentation.NewPlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newPlaylistsModule = module {

    viewModel {
        NewPlaylistViewModel(get(), get())
    }
}