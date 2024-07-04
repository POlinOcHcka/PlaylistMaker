package com.practicum.playlistmakerfinish.search.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmakerfinish.search.data.SharedPreferencesSearchHistoryRepository
import com.practicum.playlistmakerfinish.search.domain.api.TrackInteractor
import com.practicum.playlistmakerfinish.search.domain.model.Track

class SearchViewModel(private val trackInteractor: TrackInteractor, private val searchHistory: SharedPreferencesSearchHistoryRepository) : ViewModel() {

    val searchResults: MutableLiveData<List<Track>> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showNoResults: MutableLiveData<Boolean> = MutableLiveData()
    val showError: MutableLiveData<Boolean> = MutableLiveData()
    val searchHistoryTracks: MutableLiveData<List<Track>> = MutableLiveData()

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }

    fun searchTracks(query: String) {
        isLoading.value = true
        showNoResults.value = false
        showError.value = false

        trackInteractor.searchTracks(query, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>) {
                isLoading.postValue(false)
                if (foundTracks.isNotEmpty()) {
                    searchResults.postValue(foundTracks)
                } else {
                    showNoResults.postValue(true)
                }
            }

            override fun onFailure() {
                isLoading.postValue(false)
                showError.postValue(true)
            }
        })
    }

    private fun performSearch() {
        val query = searchResults.value.toString()
        searchTracks(query)
    }

    fun searchDebounce(query: String) {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed({ searchTracks(query) }, 2000L)
    }

    fun saveTrackToHistory(track: Track) {
        searchHistory.saveTrack(track)
    }

    fun loadSearchHistory() {
        val historyTracks = searchHistory.readTracks().toList()
        searchHistoryTracks.value = historyTracks
    }

    fun clearSearchHistory() {
        searchHistory.clearTracks()
        loadSearchHistory()
    }
}