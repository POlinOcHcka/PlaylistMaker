package com.practicum.playlistmakerfinish.domain.model

data class Track (
    val id: Int,
    val name: String,
    val artistName: String,
    val timeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)