package com.practicum.playlistmakerfinish.search.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmakerfinish.search.domain.SearchHistoryRepository.SearchHistoryRepository
import com.practicum.playlistmakerfinish.search.domain.api.TrackInteractor
import com.practicum.playlistmakerfinish.search.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistory: SearchHistoryRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults: StateFlow<List<Track>> get() = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _showNoResults = MutableStateFlow(false)
    val showNoResults: StateFlow<Boolean> get() = _showNoResults

    private val _showError = MutableStateFlow(false)
    val showError: StateFlow<Boolean> get() = _showError

    private val _searchHistoryTracks = MutableStateFlow<List<Track>>(emptyList())
    val searchHistoryTracks: StateFlow<List<Track>> get() = _searchHistoryTracks

    fun searchTracks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _showNoResults.value = false
            _showError.value = false

            try {
                val tracks = trackInteractor.searchTracks(query)
                _isLoading.value = false
                if (tracks.isNotEmpty()) {
                    _searchResults.value = tracks
                } else {
                    _showNoResults.value = true
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _showError.value = true
            }
        }
    }

    fun saveTrackToHistory(track: Track) {
        searchHistory.saveTrack(track)
    }

    fun loadSearchHistory() {
        val historyTracks = searchHistory.readTracks()
        _searchHistoryTracks.value = historyTracks
    }

    fun clearSearchHistory() {
        searchHistory.clearTracks()
        loadSearchHistory()
    }
}