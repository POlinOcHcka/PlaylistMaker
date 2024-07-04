package com.practicum.playlistmakerfinish.search.data.dto

import com.google.gson.annotations.SerializedName

class TrackSearchResponse(@SerializedName("results") val results: ArrayList<TrackDto>) : Response()