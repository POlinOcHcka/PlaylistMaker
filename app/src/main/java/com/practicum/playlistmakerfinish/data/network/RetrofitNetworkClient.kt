package com.practicum.playlistmakerfinish.data.network

import android.util.Log
import com.practicum.playlistmakerfinish.data.dto.Response
import com.practicum.playlistmakerfinish.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunes = retrofit.create(ItunesAPI::class.java)

    override fun doRequest(dto: Any): Response {
        Log.d("RetrofitNetworkClient", "doRequest called with dto: $dto")
        if (dto is TrackSearchRequest) {
            val resp = itunes.search(dto.expression).execute()

            val body = resp.body() ?: Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}