package com.practicum.playlistmakerfinish.library.di

import com.google.gson.Gson
import com.practicum.playlistmakerfinish.library.data.FavoriteTracksRepositoryImpl
import com.practicum.playlistmakerfinish.library.data.LocalStorageRepositoryImpl
import com.practicum.playlistmakerfinish.library.data.PlaylistsRepositoryImpl
import com.practicum.playlistmakerfinish.library.domain.FavoriteTracksRepository
import com.practicum.playlistmakerfinish.library.domain.LocalStorageRepository
import com.practicum.playlistmakerfinish.library.domain.PlaylistsRepositoty
import org.koin.dsl.module

val repositoryModule = module {
    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get()) }

    single<LocalStorageRepository> { LocalStorageRepositoryImpl(get()) }

    single<PlaylistsRepositoty> { PlaylistsRepositoryImpl(get(), get()) }

    single { Gson() }
}