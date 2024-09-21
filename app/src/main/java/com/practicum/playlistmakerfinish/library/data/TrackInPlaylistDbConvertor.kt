package com.practicum.playlistmakerfinish.library.data

import com.practicum.playlistmakerfinish.library.db.TrackInPlaylistEntity
import com.practicum.playlistmakerfinish.search.domain.model.Track

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
}