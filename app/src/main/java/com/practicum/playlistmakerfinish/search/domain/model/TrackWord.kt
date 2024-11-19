package com.practicum.playlistmakerfinish.search.domain.model

import android.view.View
import com.practicum.playlistmakerfinish.R

object TrackWord {
    fun getTrackWord(count: Int, view: View): String {
        val trackWord = when {
            count % 10 == 1 && count % 100 != 11 -> view.context.getString(R.string.word_one_track)
            count % 10 in 2..4 && (count % 100 !in 12..14) -> return view.context.getString(R.string.word_two_tracks)
            else -> view.context.getString(R.string.word_many_tracks)
        }
        return "$trackWord"
    }
}