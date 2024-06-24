package com.practicum.playlistmakerfinish.presentation.search

import android.content.SharedPreferences
import com.practicum.playlistmakerfinish.domain.SearchHistoryRepository.SearchHistoryRepository
import com.practicum.playlistmakerfinish.domain.model.Track

const val SEARCH_HISTORY_KEY = "search_history"

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