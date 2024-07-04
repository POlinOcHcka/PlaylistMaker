package com.practicum.playlistmakerfinish.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmakerfinish.player.domain.GetTrackUseCase
import com.practicum.playlistmakerfinish.player.domain.model.PlayerTrack

class PlayerViewModel(private val getTrackUseCase: GetTrackUseCase) : ViewModel() {
    private val _trackLiveData = MutableLiveData<PlayerTrack>()
    val trackLiveData: LiveData<PlayerTrack> get() = _trackLiveData

    private val _playerStateLiveData = MutableLiveData<Int>()
    val playerStateLiveData: LiveData<Int> get() = _playerStateLiveData

    fun getTrack(json: String) {
        val track = getTrackUseCase.execute(json)
        _trackLiveData.value = track
    }

    fun setPlayerState(state: Int) {
        _playerStateLiveData.value = state
    }
}