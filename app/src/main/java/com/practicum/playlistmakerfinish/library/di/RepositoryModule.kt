package com.practicum.playlistmakerfinish.library.di

import com.practicum.playlistmakerfinish.library.data.FavoriteTracksRepositoryImpl
import com.practicum.playlistmakerfinish.library.domain.FavoriteTracksRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get()) }
}