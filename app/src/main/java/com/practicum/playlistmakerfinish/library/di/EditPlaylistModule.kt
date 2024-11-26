package com.practicum.playlistmakerfinish.library.di

import com.practicum.playlistmakerfinish.library.presentation.EditPlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val editPlaylistModule = module {
    viewModel {
        EditPlaylistViewModel(get(), get(), get())
    }
}