package com.practicum.playlistmakerfinish.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmakerfinish.search.domain.SearchHistoryRepository.SearchHistoryRepository
import com.practicum.playlistmakerfinish.search.domain.model.Track

class SharedPreferencesSearchHistoryRepository(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    override fun saveTrack(track: Track) {
        val searchHistory = readTracks().toMutableList()

        searchHistory.removeAll { it.id == track.id }
        searchHistory.add(0, track)

        if (searchHistory.size > MAX_SIZE) {
            searchHistory.removeAt(MAX_SIZE)
        }

        val json = gson.toJson(searchHistory)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    override fun readTracks(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        val arrayType = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, arrayType)
    }

    override fun clearTracks() {
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_SIZE = 10
    }
}