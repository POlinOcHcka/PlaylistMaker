package com.practicum.playlistmakerfinish.data.network

import com.practicum.playlistmakerfinish.data.dto.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {
    @GET("/search?entity=song")
    fun search(@Query("term") term: String): Call<TrackSearchResponse>
}