package com.practicum.playlistmakerfinish.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmakerfinish.search.domain.SearchHistoryRepository.SearchHistoryRepository
import com.practicum.playlistmakerfinish.search.domain.api.TrackInteractor
import com.practicum.playlistmakerfinish.search.domain.model.Track
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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

    private var searchJob: Job? = null
    private val debouncePeriod: Long = 500L

    fun searchTracks(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(debouncePeriod)
            _isLoading.value = true
            _showNoResults.value = false
            _showError.value = false

            trackInteractor.searchTracks(query)
                .onEach { tracks ->
                    _isLoading.value = false
                    if (tracks.isNotEmpty()) {
                        _searchResults.value = tracks
                        _showNoResults.value = false // Заглушка не нужна, есть результаты
                    } else {
                        _searchResults.value = emptyList() // Очищаем список результатов
                        _showNoResults.value = true // Показать заглушку
                    }
                }
                .catch { e ->
                    if (e !is CancellationException) {
                        _isLoading.value = false
                        _showError.value = true
                    } else {
                        throw e
                    }
                }
                .launchIn(viewModelScope)
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