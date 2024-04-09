package com.practicum.playlistmakerfinish.SharedPreferences

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmakerfinish.model.TrackModel

const val SEARCH_HISTORY_KEY = "search_history"
const val MAX_SIZE = 10

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun saveTrack(track: MutableList<TrackModel>) {

        val searchHistory = readTracks().toMutableList()

        searchHistory.removeAll {existingTrack ->
            track.any {it.trackId == existingTrack.trackId }
        }

        searchHistory.addAll(0, track)

        if (searchHistory.size > MAX_SIZE) {
            searchHistory.removeAt(MAX_SIZE)
        }

        val json = Gson().toJson(searchHistory)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    fun readTracks(): MutableList<TrackModel> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return mutableListOf()
        val arrayType = object : TypeToken<MutableList<TrackModel>>() {}.type
        return Gson().fromJson(json, arrayType)
    }

    fun clearTracks() {
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }
}