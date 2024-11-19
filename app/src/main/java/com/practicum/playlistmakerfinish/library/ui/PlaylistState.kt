package com.practicum.playlistmakerfinish.library.ui

import com.practicum.playlistmakerfinish.library.domain.model.Playlist

sealed interface PlaylistState {
    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistState

    object Empty : PlaylistState
}