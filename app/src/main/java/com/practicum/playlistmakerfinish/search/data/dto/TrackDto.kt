package com.practicum.playlistmakerfinish.search.data.dto

import com.google.gson.annotations.SerializedName

data class TrackDto(
    @SerializedName("trackId") val trackId: Int,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val trackArtistName: String,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long,
    @SerializedName("artworkUrl100") val trackArtworkUrl100: String,
    @SerializedName("collectionName") val trackCollectionName: String,
    @SerializedName("releaseDate") val trackReleaseDate: String,
    @SerializedName("primaryGenreName") val trackPrimaryGenreName: String,
    @SerializedName("country") val trackCountry: String,
    @SerializedName("previewUrl") val trackPreviewUrl: String
)