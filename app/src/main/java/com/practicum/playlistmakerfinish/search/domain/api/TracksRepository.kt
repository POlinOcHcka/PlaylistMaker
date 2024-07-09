package com.practicum.playlistmakerfinish.search.domain.api

import com.practicum.playlistmakerfinish.search.domain.model.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}