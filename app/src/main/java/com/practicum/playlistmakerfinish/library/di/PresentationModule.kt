package com.practicum.playlistmakerfinish.library.di

import com.practicum.playlistmakerfinish.library.presentation.FavoriteTracksViewModel
import com.practicum.playlistmakerfinish.library.presentation.NewPlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { FavoriteTracksViewModel(get()) }
}