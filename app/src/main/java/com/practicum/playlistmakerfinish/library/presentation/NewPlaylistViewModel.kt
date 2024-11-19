package com.practicum.playlistmakerfinish.library.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmakerfinish.library.domain.PlaylistsInteractor
import com.practicum.playlistmakerfinish.library.domain.LocalStorageInteractor
import com.practicum.playlistmakerfinish.library.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val localStorageInteractor: LocalStorageInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) :
    ViewModel() {

    private var playlistName = ""
    private var playlistDescription: String? = null
    private var uri: String? = null

    fun saveImageToLocalStorage(uri: Uri) {
        localStorageInteractor.saveImageToLocalStorage(uri)
    }

    fun createPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.createPlaylist(
                Playlist(
                    playlistId = 0,
                    playlistName = playlistName,
                    playlistDescription = playlistDescription,
                    uri = uri,
                    tracksIdInPlaylist = emptyList(),
                    tracksCount = 0
                )
            )
        }
    }

    fun setPlaylistName(playlistName: String) {
        this.playlistName = playlistName
    }

    fun setPlaylistDescroption(playlistDescription: String) {
        this.playlistDescription = playlistDescription
    }

    fun setUri(uri: Uri?) {
        this.uri = uri?.toString()
    }
}