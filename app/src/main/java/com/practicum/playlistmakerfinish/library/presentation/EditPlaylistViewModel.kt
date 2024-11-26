package com.practicum.playlistmakerfinish.library.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmakerfinish.library.domain.GetPlaylistUseCase
import com.practicum.playlistmakerfinish.library.domain.LocalStorageInteractor
import com.practicum.playlistmakerfinish.library.domain.PlaylistsInteractor
import com.practicum.playlistmakerfinish.library.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val localStorageInteractor: LocalStorageInteractor,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val playlistsInteractor: PlaylistsInteractor,
) : NewPlaylistViewModel(localStorageInteractor, playlistsInteractor) {

    private val _playlistLiveData = MutableLiveData<Playlist>()
    val playlistLiveData: LiveData<Playlist> get() = _playlistLiveData

    private var playlistId = 0
    private var tracks: List<Int> = emptyList()

    private var playlistName = ""
    private var playlistDescription: String? = null
    private var uri: String? = null

    fun getPlaylist(json: String) {
        val playlist = getPlaylistUseCase.execute(json)
        playlist?.let {
            _playlistLiveData.postValue(it)
            playlistId = it.playlistId
            playlistName = it.playlistName
            it.playlistDescription?.let { playlistDescription = it }
            it.uri?.let { uri = it }
            tracks = it.tracksIdInPlaylist
        }
    }

    fun editPlaylist() {
        Log.d("EditPlaylistViewModel", playlistName)
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.createPlaylist(
                Playlist(
                    playlistId = playlistId,
                    playlistName = playlistName,
                    playlistDescription = playlistDescription,
                    uri = uri,
                    tracksIdInPlaylist = tracks,
                    tracksCount = tracks.size
                )
            )
        }
    }
    override fun setPlaylistName(playlistName: String) {
        this.playlistName = playlistName
    }

    override fun setPlaylistDescroption(playlistDescription: String) {
        this.playlistDescription = playlistDescription
    }

    override fun setUri(uri: Uri?) {
        this.uri = uri?.toString()
    }

}