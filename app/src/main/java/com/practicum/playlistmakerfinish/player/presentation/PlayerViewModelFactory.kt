package com.practicum.playlistmakerfinish.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmakerfinish.player.domain.GetTrackUseCase

class PlayerViewModelFactory(private val getTrackUseCase: GetTrackUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(getTrackUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}