package com.practicum.playlistmakerfinish.search.domain.api

import com.practicum.playlistmakerfinish.search.domain.model.Track

interface TrackInteractor {
    fun searchTracks(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>)
        fun onFailure()
    }
}