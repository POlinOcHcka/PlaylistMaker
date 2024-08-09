package com.practicum.playlistmakerfinish.search.data

import com.practicum.playlistmakerfinish.search.data.dto.TrackDto
import com.practicum.playlistmakerfinish.search.data.dto.TrackSearchRequest
import com.practicum.playlistmakerfinish.search.data.dto.TrackSearchResponse
import com.practicum.playlistmakerfinish.search.data.network.NetworkClient
import com.practicum.playlistmakerfinish.search.domain.api.TracksRepository
import com.practicum.playlistmakerfinish.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Flow<List<Track>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            emit((response as TrackSearchResponse).results.map { it.toDomainModel() })
        } else {
            throw Exception("Server error with code: ${response.resultCode}")
        }
    }.catch { e ->
        throw e
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