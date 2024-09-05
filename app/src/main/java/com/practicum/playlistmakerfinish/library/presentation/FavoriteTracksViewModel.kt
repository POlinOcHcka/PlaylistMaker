package com.practicum.playlistmakerfinish.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.practicum.playlistmakerfinish.library.db.TrackEntity
import com.practicum.playlistmakerfinish.library.domain.FavoriteTracksRepository

class FavoriteTracksViewModel(private val repository: FavoriteTracksRepository) : ViewModel() {

    val favoriteTracksLiveData: LiveData<List<TrackEntity>> = repository.getFavoriteTracks().asLiveData()
}