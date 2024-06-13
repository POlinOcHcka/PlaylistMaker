package com.practicum.playlistmakerfinish.data.dto

data class TrackDto (
    val trackId: Int,
    val trackName: String,
    val trackArtistName: String,
    val trackTimeMillis: Long,
    val trackArtworkUrl100: String,
    val trackCollectionName: String,
    val trackReleaseDate: String,
    val trackPrimaryGenreName: String,
    val trackCountry: String,
    val trackPreviewUrl: String
    )