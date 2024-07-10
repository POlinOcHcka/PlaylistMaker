package com.practicum.playlistmakerfinish.search.ui

import com.practicum.playlistmakerfinish.search.domain.SearchHistoryRepository.SearchHistoryRepository
import com.practicum.playlistmakerfinish.search.domain.model.Track

class SearchHistory(private val searchHistoryRepository: SearchHistoryRepository) {

    fun saveTrack(track: Track) {
        searchHistoryRepository.saveTrack(track)
    }

    fun readTracks(): List<Track> {
        return searchHistoryRepository.readTracks()
    }

    fun clearTracks() {
        searchHistoryRepository.clearTracks()
    }
}