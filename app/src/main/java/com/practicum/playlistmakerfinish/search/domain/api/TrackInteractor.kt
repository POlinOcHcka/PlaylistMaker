package com.practicum.playlistmakerfinish.search.domain.api

import com.practicum.playlistmakerfinish.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTracks(expression: String): Flow<List<Track>>
}