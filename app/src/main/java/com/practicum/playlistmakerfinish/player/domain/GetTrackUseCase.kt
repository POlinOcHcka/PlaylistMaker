package com.practicum.playlistmakerfinish.player.domain

class GetTrackUseCase(private val repository: TrackRepository) {
    fun execute(json: String): PlayerTrack? {
        return repository.getTrackFromJson(json)
    }
}