package com.practicum.playlistmakerfinish.search.data.network

import com.practicum.playlistmakerfinish.search.data.dto.Response
import com.practicum.playlistmakerfinish.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunes = retrofit.create(ItunesAPI::class.java)

    override fun doRequest(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            val resp = itunes.search(dto.expression).execute()
            val body = resp.body() ?: Response(resp.code())
            body.apply { resultCode = resp.code() }
        } else {
            Response(400)
        }
    }
}