package com.practicum.playlistmakerfinish.player.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmakerfinish.player.domain.GetTrackUseCase
import com.practicum.playlistmakerfinish.player.domain.PlayerTrack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PlayerViewModel(private val getTrackUseCase: GetTrackUseCase) : ViewModel() {

    private val _trackLiveData = MutableLiveData<PlayerTrack?>()
    val trackLiveData: LiveData<PlayerTrack?> get() = _trackLiveData

    private val _playerStateLiveData = MutableLiveData<Int>()
    val playerStateLiveData: LiveData<Int> get() = _playerStateLiveData

    private val _currentTimeLiveData = MutableLiveData<String>()
    val currentTimeLiveData: LiveData<String> get() = _currentTimeLiveData

    private var updateJob: Job? = null

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    companion object {
        private const val UPDATE_INTERVAL = 300L
    }

    fun getTrack(json: String) {
        val track = getTrackUseCase.execute(json)
        _trackLiveData.value = track
    }

    fun setPlayerState(state: Int) {
        _playerStateLiveData.value = state
    }

    fun startUpdatingProgress(mediaPlayer: MediaPlayer) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (isActive) {
                val formattedTime = dateFormat.format(mediaPlayer.currentPosition)
                _currentTimeLiveData.postValue(formattedTime)
                delay(UPDATE_INTERVAL)
            }
        }
    }

    fun stopUpdatingProgress() {
        updateJob?.cancel()
    }
}