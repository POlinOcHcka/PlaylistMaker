package com.practicum.playlistmakerfinish.data

import com.practicum.playlistmakerfinish.data.dto.TrackDto
import com.practicum.playlistmakerfinish.data.dto.TrackSearchRequest
import com.practicum.playlistmakerfinish.data.dto.TrackSearchResponse
import com.practicum.playlistmakerfinish.data.network.NetworkClient
import com.practicum.playlistmakerfinish.domain.api.TracksRepository
import com.practicum.playlistmakerfinish.domain.model.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.map { it.toDomainModel() }
        } else {
            throw Exception("Server error with code: ${response.resultCode}")
        }
    }
}

fun TrackDto.toDomainModel(): Track {
    return Track(
        id = this.trackId,
        name = this.trackName,
        artistName = this.trackArtistName,
        timeMillis = this.trackTimeMillis,
        artworkUrl100 = this.trackArtworkUrl100,
        collectionName = this.trackCollectionName,
        releaseDate = this.trackReleaseDate,
        primaryGenreName = this.trackPrimaryGenreName,
        country = this.trackCountry,
        previewUrl = this.trackPreviewUrl
    )
}