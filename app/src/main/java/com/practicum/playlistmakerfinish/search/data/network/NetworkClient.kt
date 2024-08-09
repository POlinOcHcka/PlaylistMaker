package com.practicum.playlistmakerfinish.search.data.network

import com.practicum.playlistmakerfinish.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}