package com.practicum.playlistmakerfinish.domain.SearchHistoryRepository

import com.practicum.playlistmakerfinish.domain.model.Track

interface SearchHistoryRepository {
    fun saveTrack(track: Track)
    fun readTracks(): List<Track>
    fun clearTracks()
}