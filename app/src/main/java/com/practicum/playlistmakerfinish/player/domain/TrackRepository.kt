package com.practicum.playlistmakerfinish.player.domain

interface TrackRepository {
    fun getTrackFromJson(json: String): PlayerTrack?
}