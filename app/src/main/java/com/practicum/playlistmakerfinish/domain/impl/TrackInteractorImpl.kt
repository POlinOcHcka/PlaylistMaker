package com.practicum.playlistmakerfinish.domain.impl

import com.practicum.playlistmakerfinish.domain.api.TrackInteractor
import com.practicum.playlistmakerfinish.domain.api.TracksRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TracksRepository) : TrackInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            try {
                val tracks = repository.searchTracks(expression)
                consumer.consume(tracks)
            } catch (e: Exception) {
                consumer.onFailure()
            }
        }
    }
}