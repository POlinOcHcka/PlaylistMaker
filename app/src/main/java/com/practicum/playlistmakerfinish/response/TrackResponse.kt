package com.practicum.playlistmakerfinish.response

import com.practicum.playlistmakerfinish.model.TrackModel

data class TrackResponse(
        val resultCount: Int,
        val results: ArrayList<TrackModel>)