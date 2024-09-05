package com.practicum.playlistmakerfinish.library.data

import com.practicum.playlistmakerfinish.library.db.FavoriteTracksDao
import com.practicum.playlistmakerfinish.library.db.TrackEntity
import com.practicum.playlistmakerfinish.library.domain.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow

class FavoriteTracksRepositoryImpl(
    private val favoriteTracksDao: FavoriteTracksDao
) : FavoriteTracksRepository {

    override suspend fun addTrack(track: TrackEntity) {
        favoriteTracksDao.addTrackToFavorites(track)
    }

    override suspend fun removeTrack(track: TrackEntity) {
        favoriteTracksDao.removeTrackFromFavorites(track)
    }

    override fun getFavoriteTracks(): Flow<List<TrackEntity>> {
        return favoriteTracksDao.getAllFavoriteTracks()
    }

    override suspend fun getAllFavoriteTrackIds(): List<String> {
        return favoriteTracksDao.getAllFavoriteTrackIds()
    }
}