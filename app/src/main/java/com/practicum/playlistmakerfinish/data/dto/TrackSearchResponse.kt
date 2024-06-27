package com.practicum.playlistmakerfinish.data.dto

import com.google.gson.annotations.SerializedName

class TrackSearchResponse(@SerializedName("results") val results: ArrayList<TrackDto>) : Response()