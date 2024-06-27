package com.practicum.playlistmakerfinish.domain.api

import com.practicum.playlistmakerfinish.domain.model.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}