package com.practicum.playlistmakerfinish.ServiceLocator

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmakerfinish.app.domain.GetThemeUseCase
import com.practicum.playlistmakerfinish.app.domain.SetThemeUseCase
import com.practicum.playlistmakerfinish.app.data.ThemeRepository
import com.practicum.playlistmakerfinish.app.data.ThemeRepositoryImpl
import com.practicum.playlistmakerfinish.app.ui.App
import com.practicum.playlistmakerfinish.player.data.TrackRepository
import com.practicum.playlistmakerfinish.player.domain.GetTrackUseCase
import com.practicum.playlistmakerfinish.player.presentation.PlayerViewModelFactory
import com.practicum.playlistmakerfinish.search.data.SharedPreferencesSearchHistoryRepository
import com.practicum.playlistmakerfinish.search.data.TracksRepositoryImpl
import com.practicum.playlistmakerfinish.search.data.network.NetworkClient
import com.practicum.playlistmakerfinish.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmakerfinish.search.domain.SearchHistoryRepository.SearchHistoryRepository
import com.practicum.playlistmakerfinish.search.domain.api.TrackInteractor
import com.practicum.playlistmakerfinish.search.domain.api.TracksRepository
import com.practicum.playlistmakerfinish.search.domain.impl.TrackInteractorImpl
import com.practicum.playlistmakerfinish.search.domain.model.IntentKeys.SEARCH_HISTORY_KEY
import com.practicum.playlistmakerfinish.settings.data.SettingsRepository
import com.practicum.playlistmakerfinish.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmakerfinish.settings.domain.SwitchThemeUseCase

object ServiceLocator {

    private val sharedPreferences: SharedPreferences by lazy {
        App.instance.getSharedPreferences(SEARCH_HISTORY_KEY, Context.MODE_PRIVATE)
    }

    private val searchHistoryRepository: SearchHistoryRepository by lazy {
        SharedPreferencesSearchHistoryRepository(sharedPreferences)
    }

    private val networkClient: NetworkClient by lazy {
        RetrofitNetworkClient()
    }

    private val tracksRepository: TracksRepository by lazy {
        TracksRepositoryImpl(networkClient)
    }

    private val trackInteractor: TrackInteractor by lazy {
        TrackInteractorImpl(tracksRepository)
    }

    fun provideTrackInteractor(): TrackInteractor {
        return trackInteractor
    }

    private val trackRepository: TrackRepository by lazy {
        TrackRepository()
    }

    private val getTrackUseCase: GetTrackUseCase by lazy {
        GetTrackUseCase(trackRepository)
    }

    private val playerViewModelFactory: PlayerViewModelFactory by lazy {
        PlayerViewModelFactory(getTrackUseCase)
    }

    fun providePlayerViewModelFactory(): ViewModelProvider.Factory {
        return playerViewModelFactory
    }

    fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return searchHistoryRepository
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideSwitchThemeUseCase(context: Context): SwitchThemeUseCase {
        return SwitchThemeUseCase(getSettingsRepository(context))
    }

    private fun getThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(context)
    }

    fun provideGetThemeUseCase(context: Context): GetThemeUseCase {
        return GetThemeUseCase(getThemeRepository(context))
    }

    fun provideSetThemeUseCase(context: Context): SetThemeUseCase {
        return SetThemeUseCase(getThemeRepository(context))
    }
}