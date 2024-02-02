package com.practicum.playlistmakerfinish.api

import com.practicum.playlistmakerfinish.response.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {
    @GET("/search?entity=song")
    fun search(@Query("term") term: String): Call<TrackResponse>
}