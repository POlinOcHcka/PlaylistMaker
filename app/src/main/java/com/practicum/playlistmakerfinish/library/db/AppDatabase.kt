package com.practicum.playlistmakerfinish.library.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun getPlaylistDao(): PlaylistDao
    abstract fun getTrackInPlaylistDao(): TrackInPlaylistDao
}