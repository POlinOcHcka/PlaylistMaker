package com.practicum.playlistmakerfinish.player.data

import com.google.gson.Gson
import com.practicum.playlistmakerfinish.player.domain.model.PlayerTrack

class TrackRepository {
    fun getTrackFromJson(json: String): PlayerTrack? {
        return Gson().fromJson(json, PlayerTrack::class.java)
    }
}