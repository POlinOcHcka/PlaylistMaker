package com.practicum.playlistmakerfinish.library.domain

import com.practicum.playlistmakerfinish.library.db.TrackEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addTrack(track: TrackEntity)
    suspend fun removeTrack(track: TrackEntity)
    fun getFavoriteTracks(): Flow<List<TrackEntity>>
    suspend fun getAllFavoriteTrackIds(): List<String>
}