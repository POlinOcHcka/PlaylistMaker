package com.practicum.playlistmakerfinish.player.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmakerfinish.library.db.TrackEntity
import com.practicum.playlistmakerfinish.library.domain.FavoriteTracksRepository
import com.practicum.playlistmakerfinish.library.domain.model.Playlist
import com.practicum.playlistmakerfinish.library.domain.PlaylistsInteractor
import com.practicum.playlistmakerfinish.player.domain.GetTrackUseCase
import com.practicum.playlistmakerfinish.player.domain.model.PlayerTrack
import com.practicum.playlistmakerfinish.player.ui.TrackInPlaylistState
import com.practicum.playlistmakerfinish.search.domain.model.Track
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class PlayerViewModel(
    private val getTrackUseCase: GetTrackUseCase,
    private val favoriteTracksRepository: FavoriteTracksRepository,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _trackLiveData = MutableLiveData<PlayerTrack?>()
    val trackLiveData: LiveData<PlayerTrack?> get() = _trackLiveData

    private val _playerStateLiveData = MutableLiveData<Int>()
    val playerStateLiveData: LiveData<Int> get() = _playerStateLiveData

    private val _currentTimeLiveData = MutableLiveData<String>()
    val currentTimeLiveData: LiveData<String> get() = _currentTimeLiveData

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    fun observePlaylists(): LiveData<List<Playlist>> = playlistsLiveData

    private val trackInPlaylistLiveData = MutableLiveData<TrackInPlaylistState>()
    fun observeTrackInPlaylistState(): LiveData<TrackInPlaylistState> = trackInPlaylistLiveData

    private var updateJob: Job? = null

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    companion object {
        private const val UPDATE_INTERVAL = 300L
    }

    fun getTrack(json: String) {
        val track = getTrackUseCase.execute(json)
        track?.let {
            viewModelScope.launch {
                it.isFavorite = isTrackFavorite(it.id)
                _trackLiveData.postValue(it)
            }
        }
    }

    fun toggleFavorite(track: PlayerTrack) {
        viewModelScope.launch {
            track.isFavorite = !track.isFavorite
            if (track.isFavorite) {
                favoriteTracksRepository.addTrack(track.toEntity())
            } else {
                favoriteTracksRepository.removeTrack(track.toEntity())
            }
            _trackLiveData.value = track
        }
    }

    private suspend fun isTrackFavorite(trackId: String): Boolean {
        val favoriteTrackIds = favoriteTracksRepository.getAllFavoriteTrackIds()
        return favoriteTrackIds.contains(trackId)
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

    fun getSavedPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getSavedPlaylists().collect { playlists ->
                playlistsLiveData.postValue(playlists)
            }
        }
    }

    fun addTracksIdInPlaylist(playlist: Playlist, tracksId: List<Int>, track: Track) {
        if (track.id in tracksId) {
            trackInPlaylistLiveData.postValue(TrackInPlaylistState.TrackIsAlreadyInPlaylist(playlist.playlistName))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                playlistsInteractor.addTracksIdInPlaylist(playlist, tracksId, track)
            }
            trackInPlaylistLiveData.postValue(TrackInPlaylistState.TrackAddToPlaylist(playlist.playlistName))
        }
    }
}

fun PlayerTrack.toEntity() = TrackEntity(
    trackId = this.id,
    coverUrl = this.artworkUrl100,
    trackName = this.name,
    artistName = this.artistName,
    albumName = this.collectionName,
    releaseYear = this.releaseDate,
    genre = this.primaryGenreName,
    country = this.country,
    duration = this.timeMillis.toString(),
    trackUrl = this.previewUrl
)