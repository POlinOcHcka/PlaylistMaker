package com.practicum.playlistmakerfinish.search.domain.api

import com.practicum.playlistmakerfinish.search.domain.model.Track

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
}