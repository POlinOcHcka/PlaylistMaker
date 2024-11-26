package com.practicum.playlistmakerfinish.library.data

import android.util.Log
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.library.data.PlaylistDbConvertor.toPlaylist
import com.practicum.playlistmakerfinish.library.data.PlaylistDbConvertor.toPlaylistEntity
import com.practicum.playlistmakerfinish.library.data.TrackInPlaylistDbConvertor.toTrack
import com.practicum.playlistmakerfinish.library.data.TrackInPlaylistDbConvertor.toTrackInPlaylistEntity
import com.practicum.playlistmakerfinish.library.db.AppDatabase
import com.practicum.playlistmakerfinish.library.domain.PlaylistsRepositoty
import com.practicum.playlistmakerfinish.library.domain.model.Playlist
import com.practicum.playlistmakerfinish.library.domain.model.Track

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase, private val gson: Gson) :
    PlaylistsRepositoty {
    override suspend fun createPlaylist(playlist: Playlist) {
        Log.d("PlaylistsRepositoryImpl", playlist.toString())
        appDatabase.getPlaylistDao().insertPlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun addTrackIdInPlaylist(
        playlist: Playlist,
        tracksId: List<Int>,
        track: Track,
    ) {
        appDatabase.getTrackInPlaylistDao().insertTrackInPlaylist(track.toTrackInPlaylistEntity())
        val updatedTracksId = tracksId + track.id
        val updatedPlaylist = playlist.copy(
            tracksIdInPlaylist = updatedTracksId, tracksCount = playlist.tracksCount + 1
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

    override fun getSavedPlaylist(id: Int): Flow<Playlist> {
        return appDatabase.getPlaylistDao().getSavedPlaylist(id).map { it.toPlaylist() }
    }

    override fun getPlaylistFromJson(json: String): Playlist? {
        return gson.fromJson(json, Playlist::class.java)
    }

    override fun getTracksInPlaylist(trackIds: List<Int>): Flow<List<Track>> {
        return appDatabase.getTrackInPlaylistDao().getTracksInPlaylist(trackIds)
            .map { tracks -> tracks.map { it.toTrack() } }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val tracks = playlist.tracksIdInPlaylist
        appDatabase.getPlaylistDao().removePlaylist(playlist.playlistId)
        val plalists = appDatabase.getPlaylistDao().getSavedPlaylists().first()
        for (trackId: Int in tracks) {
            var isExist = false
            for (playlistOther in plalists) {
                if (playlistOther.tracksIdInPlaylist.contains(trackId.toString())) isExist = true
            }
            if (!isExist) appDatabase.getTrackInPlaylistDao().removeTrack(trackId)
        }
    }

    override suspend fun removeTrack(playlist: Playlist, trackId: Int) {
        val plalists = appDatabase.getPlaylistDao().getSavedPlaylists().first()
        var isExist = false
        for (playlistOther in plalists) {
            if (playlistOther.tracksIdInPlaylist.contains(trackId.toString())) isExist = true
        }
        if (!isExist) appDatabase.getTrackInPlaylistDao().removeTrack(trackId)
        val updatedPlaylist = playlist.copy(
            tracksIdInPlaylist = playlist.tracksIdInPlaylist.filter { it == trackId }, tracksCount = playlist.tracksCount - 1
        )
        appDatabase.getPlaylistDao().updatePlaylist(updatedPlaylist.toPlaylistEntity())
    }
}