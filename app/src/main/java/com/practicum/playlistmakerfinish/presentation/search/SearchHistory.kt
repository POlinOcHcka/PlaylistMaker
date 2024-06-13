package com.practicum.playlistmakerfinish.presentation.search

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmakerfinish.domain.model.Track

const val SEARCH_HISTORY_KEY = "search_history"
const val MAX_SIZE = 10

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun saveTrack(track: Track) {

        val searchHistory = readTracks().toMutableList()

        searchHistory.removeAll {it.id == track.id}

        searchHistory.addAll(0, listOf(track))

        if (searchHistory.size > MAX_SIZE) {
            searchHistory.removeAt(MAX_SIZE)
        }

        val json = Gson().toJson(searchHistory)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()

        Log.d("SearchHistory", "Track saved to history: $track")
    }

    fun readTracks(): MutableList<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return mutableListOf()
        val arrayType = object : TypeToken<MutableList<Track>>() {}.type
        Log.d("SearchHistory", "Track read")
        return Gson().fromJson(json, arrayType)
    }

    fun clearTracks() {
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }
}