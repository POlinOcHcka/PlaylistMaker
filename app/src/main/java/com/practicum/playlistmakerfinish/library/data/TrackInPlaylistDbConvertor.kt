package com.practicum.playlistmakerfinish.library.data

import com.practicum.playlistmakerfinish.library.db.TrackInPlaylistEntity
import com.practicum.playlistmakerfinish.library.domain.model.Track

object TrackInPlaylistDbConvertor {
    fun Track.toTrackInPlaylistEntity(): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            id,
            name,
            artistName,
            timeMillis,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl
        )
    }

    fun TrackInPlaylistEntity.toTrack(): Track {
        return Track(
            trackId,
            trackName,
            artistName,
            trackTime,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl ?: "",
        )
    }
}