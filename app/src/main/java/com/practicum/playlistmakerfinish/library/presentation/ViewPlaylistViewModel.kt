package com.practicum.playlistmakerfinish.library.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.library.domain.GetPlaylistUseCase
import com.practicum.playlistmakerfinish.library.domain.PlaylistsInteractor
import com.practicum.playlistmakerfinish.library.domain.model.Playlist
import com.practicum.playlistmakerfinish.library.domain.model.Track
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class ViewPlaylistViewModel(
    application: Application,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val playlistInteractor: PlaylistsInteractor,

    ) : AndroidViewModel(application) {

    private val _playlistLiveData = MutableLiveData<Playlist>()
    val playlistLiveData: LiveData<Playlist> get() = _playlistLiveData
    private val _trackTimeLiveData = MutableLiveData<Long>()
    val trackTimeLiveData: LiveData<Long> = _trackTimeLiveData
    private val _trackListLiveData = MutableLiveData<List<Track>>()
    val trackListLiveData: LiveData<List<Track>> = _trackListLiveData


    fun getPlaylist(json: String) {
        val playlist = getPlaylistUseCase.execute(json)
        Log.d("ViewPlaylistViewModel", playlist.toString())
        playlist?.let {
            _playlistLiveData.postValue(it)
            viewModelScope.launch {
                playlistInteractor.getTracksInPlaylist(playlist.tracksIdInPlaylist)
                    .collect { tracks ->
                        _trackTimeLiveData.postValue(TimeUnit.MILLISECONDS.toMinutes(tracks.map { it.timeMillis }
                            .sum()))
                        _trackListLiveData.postValue(tracks)
                    }
            }

        }
    }

    fun getMessage(): String? {
        val tracks = trackListLiveData.value
        if (tracks.isNullOrEmpty()) return null
        val playlist = playlistLiveData.value
        playlist?.let {
            val sb = StringBuilder(playlist.playlistName)
            sb.append("\n")
            playlist.playlistDescription?.let { sb.appendLine(playlist.playlistDescription) }
            sb.appendLine(
                getApplication<Application>().resources.getQuantityString(
                    R.plurals.tracks,
                    playlist.tracksIdInPlaylist.size,
                    playlist.tracksIdInPlaylist.size
                )
            )
            val PATTERN = "mm:ss"
            val formatter = DateTimeFormatter.ofPattern(PATTERN)
            for ((index, track: Track) in tracks.withIndex()) {
                val localDateTime =
                    Instant.ofEpochMilli(track.timeMillis).atZone(ZoneOffset.UTC).toLocalDateTime()

                sb.appendLine("${index + 1}.${track.artistName} - ${track.name}(${localDateTime.format(formatter)})")
            }
            return sb.toString()
        }
        return null
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            _playlistLiveData.value?.let {
                playlistInteractor.deletePlaylist(it)
            }
        }
    }

    fun removeTrack(trackId: Int) {
        viewModelScope.launch {
            _playlistLiveData.value?.let {
                playlistInteractor.removeTrack(it, trackId)
            }
        }
    }

    private companion object {
        const val TAG = "ViewPlaylistViewModel"
    }
}