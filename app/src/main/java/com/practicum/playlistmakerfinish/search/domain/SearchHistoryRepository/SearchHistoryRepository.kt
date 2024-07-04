package com.practicum.playlistmakerfinish.search.domain.SearchHistoryRepository

import com.practicum.playlistmakerfinish.search.domain.model.Track

interface SearchHistoryRepository {
    fun saveTrack(track: Track)
    fun readTracks(): List<Track>
    fun clearTracks()
}