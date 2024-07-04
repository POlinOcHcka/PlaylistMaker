package com.practicum.playlistmakerfinish

import com.practicum.playlistmakerfinish.search.data.TracksRepositoryImpl
import com.practicum.playlistmakerfinish.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmakerfinish.search.domain.api.TrackInteractor
import com.practicum.playlistmakerfinish.search.domain.api.TracksRepository
import com.practicum.playlistmakerfinish.search.domain.impl.TrackInteractorImpl

object ServiceLocator {
    private fun getTrackRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }
}