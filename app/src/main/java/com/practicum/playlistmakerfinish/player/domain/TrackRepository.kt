package com.practicum.playlistmakerfinish.player.domain

import com.practicum.playlistmakerfinish.player.domain.model.PlayerTrack
import com.practicum.playlistmakerfinish.search.domain.model.Track

interface TrackRepository {
    fun getTrackFromJson(json: String): PlayerTrack?
}