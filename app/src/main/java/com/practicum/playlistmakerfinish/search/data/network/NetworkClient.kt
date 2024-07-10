package com.practicum.playlistmakerfinish.search.data.network

import com.practicum.playlistmakerfinish.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}