package com.practicum.playlistmakerfinish.data.network

import com.practicum.playlistmakerfinish.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}