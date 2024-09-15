package com.practicum.playlistmakerfinish.player.ui

sealed interface TrackInPlaylistState {
    data class TrackIsAlreadyInPlaylist(val playlistName: String) : TrackInPlaylistState
    data class TrackAddToPlaylist(val playlistName: String) : TrackInPlaylistState
}