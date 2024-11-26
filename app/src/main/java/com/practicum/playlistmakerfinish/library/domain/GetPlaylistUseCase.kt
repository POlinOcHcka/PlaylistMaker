package com.practicum.playlistmakerfinish.library.domain

import com.practicum.playlistmakerfinish.library.domain.model.Playlist

class GetPlaylistUseCase(private val repository: PlaylistsRepositoty) {
    fun execute(json: String): Playlist? {
        return repository.getPlaylistFromJson(json)
    }
}