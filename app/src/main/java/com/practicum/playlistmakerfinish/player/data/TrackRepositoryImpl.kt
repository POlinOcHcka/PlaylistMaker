package com.practicum.playlistmakerfinish.player.data

import com.google.gson.Gson
import com.practicum.playlistmakerfinish.player.domain.PlayerTrack
import com.practicum.playlistmakerfinish.player.domain.TrackRepository

class TrackRepositoryImpl(private val gson: Gson) : TrackRepository {
    override fun getTrackFromJson(json: String): PlayerTrack? {
        return gson.fromJson(json, PlayerTrack::class.java)
    }
}