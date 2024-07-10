package com.practicum.playlistmakerfinish.player.data

import com.google.gson.Gson
import com.practicum.playlistmakerfinish.player.domain.PlayerTrack
import com.practicum.playlistmakerfinish.player.domain.TrackRepository

class TrackRepositoryImpl : TrackRepository {
    override fun getTrackFromJson(json: String): PlayerTrack? {
        return Gson().fromJson(json, PlayerTrack::class.java)
    }
}