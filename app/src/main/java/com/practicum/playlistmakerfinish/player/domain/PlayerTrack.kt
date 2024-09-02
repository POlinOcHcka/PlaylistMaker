package com.practicum.playlistmakerfinish.player.domain

data class PlayerTrack(
    val id: String,
    val name: String,
    val artistName: String,
    val timeMillis: Long,
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val artworkUrl100: String,
    val previewUrl: String,
    var isFavorite: Boolean = false
)