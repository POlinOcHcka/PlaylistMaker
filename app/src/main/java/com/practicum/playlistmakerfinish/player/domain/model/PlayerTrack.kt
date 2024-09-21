package com.practicum.playlistmakerfinish.player.domain.model

import com.practicum.playlistmakerfinish.search.domain.model.Track

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

fun PlayerTrack.toTrack(): Track {
    return Track(
        id = this.id.toInt(),
        name = this.name,
        artistName = this.artistName,
        timeMillis = this.timeMillis,
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.previewUrl,
        isFavorite = this.isFavorite
    )
}