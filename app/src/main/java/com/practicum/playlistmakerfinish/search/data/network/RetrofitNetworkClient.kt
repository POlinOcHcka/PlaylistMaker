package com.practicum.playlistmakerfinish.search.data.network

import com.practicum.playlistmakerfinish.search.data.dto.Response
import com.practicum.playlistmakerfinish.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val itunesAPI: ItunesAPI) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return try {
            if (dto is TrackSearchRequest) {
                val response = itunesAPI.search(dto.expression)
                response.resultCode = 200
                response
            } else {
                Response(400)
            }
        } catch (e: Exception) {
            Response(500)
        }
    }
}