package com.practicum.playlistmakerfinish.library.data

import com.practicum.playlistmakerfinish.library.data.PlaylistDbConvertor.toPlaylist
import com.practicum.playlistmakerfinish.library.data.PlaylistDbConvertor.toPlaylistEntity
import com.practicum.playlistmakerfinish.library.data.TrackInPlaylistDbConvertor.toTrackInPlaylistEntity
import com.practicum.playlistmakerfinish.library.db.AppDatabase
import com.practicum.playlistmakerfinish.library.domain.model.Playlist
import com.practicum.playlistmakerfinish.library.domain.PlaylistsRepositoty
import com.practicum.playlistmakerfinish.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase) : PlaylistsRepositoty {
    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.getPlaylistDao().insertPlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun addTrackIdInPlaylist(
        playlist: Playlist,
        tracksId: List<Int>,
        track: Track
    ) {
        appDatabase.getTrackInPlaylistDao().insertTrackInPlaylist(track.toTrackInPlaylistEntity())
        val updatedTracksId = tracksId+ track.id
        val updatedPlaylist = playlist.copy(
            tracksIdInPlaylist = updatedTracksId,
            tracksCount = playlist.tracksCount + 1
        )
        appDatabase.getPlaylistDao().updatePlaylist(updatedPlaylist.toPlaylistEntity())
    }

    override fun getSavedPlaylists(): Flow<List<Playlist>> {
        return appDatabase.getPlaylistDao().getSavedPlaylists().map {
            it.map { entity -> entity.toPlaylist() }
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.getPlaylistDao().updatePlaylist(playlist.toPlaylistEntity())
    }
}