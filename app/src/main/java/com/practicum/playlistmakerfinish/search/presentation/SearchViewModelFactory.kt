package com.practicum.playlistmakerfinish.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmakerfinish.search.data.SharedPreferencesSearchHistoryRepository
import com.practicum.playlistmakerfinish.search.domain.api.TrackInteractor

class SearchViewModelFactory(private val trackInteractor: TrackInteractor, private val searchHistory: SharedPreferencesSearchHistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(trackInteractor, searchHistory) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}