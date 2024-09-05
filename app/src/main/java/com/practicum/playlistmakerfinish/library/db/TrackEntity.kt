package com.practicum.playlistmakerfinish.library.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class TrackEntity(
    @PrimaryKey val trackId: String,
    val coverUrl: String,
    val trackName: String,
    val artistName: String,
    val albumName: String?,
    val releaseYear: String,
    val genre: String,
    val country: String,
    val duration: String,
    val trackUrl: String
)