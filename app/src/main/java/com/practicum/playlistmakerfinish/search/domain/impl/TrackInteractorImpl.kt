package com.practicum.playlistmakerfinish.search.domain.impl

import com.practicum.playlistmakerfinish.search.domain.api.TrackInteractor
import com.practicum.playlistmakerfinish.search.domain.api.TracksRepository
import com.practicum.playlistmakerfinish.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TracksRepository) : TrackInteractor {

    override fun searchTracks(expression: String): Flow<List<Track>> {
        return repository.searchTracks(expression)
    }
}