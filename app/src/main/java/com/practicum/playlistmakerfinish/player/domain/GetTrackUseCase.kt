package com.practicum.playlistmakerfinish.player.domain

import com.practicum.playlistmakerfinish.player.data.TrackRepository

class GetTrackUseCase(private val repository: TrackRepository) {
    fun execute(json: String): PlayerTrack? {
        return repository.getTrackFromJson(json)
    }
}