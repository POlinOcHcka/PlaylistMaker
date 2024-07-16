package com.practicum.playlistmakerfinish.search.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.search.data.SharedPreferencesSearchHistoryRepository
import com.practicum.playlistmakerfinish.search.data.TracksRepositoryImpl
import com.practicum.playlistmakerfinish.search.data.network.ItunesAPI
import com.practicum.playlistmakerfinish.search.data.network.NetworkClient
import com.practicum.playlistmakerfinish.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmakerfinish.search.domain.SearchHistoryRepository.SearchHistoryRepository
import com.practicum.playlistmakerfinish.search.domain.api.TrackInteractor
import com.practicum.playlistmakerfinish.search.domain.api.TracksRepository
import com.practicum.playlistmakerfinish.search.domain.impl.TrackInteractorImpl
import com.practicum.playlistmakerfinish.search.presentation.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {
    single<ItunesAPI> {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ItunesAPI::class.java)
    }
    single<NetworkClient> { RetrofitNetworkClient(get()) }
    single { Gson() }
    single<SearchHistoryRepository> {
        SharedPreferencesSearchHistoryRepository(
            androidContext().getSharedPreferences("settings", Context.MODE_PRIVATE),
            get()
        )
    }
    single<TracksRepository> { TracksRepositoryImpl(get()) }
    factory<TrackInteractor> { TrackInteractorImpl(get()) }
    viewModel { SearchViewModel(get(), get()) }
}