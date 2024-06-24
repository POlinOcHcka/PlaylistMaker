package com.practicum.playlistmakerfinish

import com.practicum.playlistmakerfinish.data.TracksRepositoryImpl
import com.practicum.playlistmakerfinish.data.network.RetrofitNetworkClient
import com.practicum.playlistmakerfinish.domain.api.TrackInteractor
import com.practicum.playlistmakerfinish.domain.api.TracksRepository
import com.practicum.playlistmakerfinish.domain.impl.TrackInteractorImpl

object ServiceLocator {
    private fun getTrackRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }
}