package com.practicum.playlistmakerfinish.library.domain

import com.practicum.playlistmakerfinish.library.domain.model.Playlist
import com.practicum.playlistmakerfinish.library.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepositoty {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun addTrackIdInPlaylist(playlist: Playlist, tracksId: List<Int>, track: Track)
    fun getSavedPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylist(playlist: Playlist)
    fun getSavedPlaylist(id: Int): Flow<Playlist>
    fun getPlaylistFromJson(json: String): Playlist?
    fun getTracksInPlaylist(trackIds: List<Int>): Flow<List<Track>>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun removeTrack(playlist: Playlist, trackId: Int)
}