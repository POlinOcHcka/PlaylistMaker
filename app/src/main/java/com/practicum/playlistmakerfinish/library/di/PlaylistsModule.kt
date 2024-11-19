package com.practicum.playlistmakerfinish.library.di

import com.practicum.playlistmakerfinish.library.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistsModule = module {

    viewModel {
        PlaylistsViewModel(get())
    }
}