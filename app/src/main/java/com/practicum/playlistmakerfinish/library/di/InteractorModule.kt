package com.practicum.playlistmakerfinish.library.di

import com.practicum.playlistmakerfinish.library.domain.LocalStorageInteractor
import com.practicum.playlistmakerfinish.library.domain.LocalStorageInteractorImpl
import com.practicum.playlistmakerfinish.library.domain.PlaylistsInteractor
import com.practicum.playlistmakerfinish.library.domain.PlaylistsInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<PlaylistsInteractor> { PlaylistsInteractorImpl(get()) }

    single<LocalStorageInteractor> { LocalStorageInteractorImpl(get()) }
}