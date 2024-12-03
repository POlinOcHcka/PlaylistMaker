package com.practicum.playlistmakerfinish.library.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

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
    val trackUrl: String,
    val createdAt: Long? = Instant.now().toEpochMilli(),
)