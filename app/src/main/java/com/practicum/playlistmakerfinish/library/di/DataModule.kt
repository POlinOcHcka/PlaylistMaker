package com.practicum.playlistmakerfinish.library.di

import android.app.Application
import androidx.room.Room
import com.practicum.playlistmakerfinish.library.db.AppDatabase
import org.koin.dsl.module

val dataModule = module {

    single {
        Room.databaseBuilder(
            get<Application>(),
            AppDatabase::class.java, "app-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().favoriteTracksDao() }
    single { get<AppDatabase>().getPlaylistDao() }
    single { get<AppDatabase>().getTrackInPlaylistDao() }
}