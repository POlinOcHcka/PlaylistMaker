package com.practicum.playlistmakerfinish.search.domain.api

import com.practicum.playlistmakerfinish.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<List<Track>>
}