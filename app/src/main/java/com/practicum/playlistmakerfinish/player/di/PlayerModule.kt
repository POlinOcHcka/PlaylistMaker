package com.practicum.playlistmakerfinish.player.di

import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.player.data.TrackRepositoryImpl
import com.practicum.playlistmakerfinish.player.domain.GetTrackUseCase
import com.practicum.playlistmakerfinish.player.domain.TrackRepository
import com.practicum.playlistmakerfinish.player.presentation.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    factory { GetTrackUseCase(get()) }
    viewModel { PlayerViewModel(get()) }
    single { Gson() }
    factory { MediaPlayer() }
}