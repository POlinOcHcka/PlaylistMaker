package com.practicum.playlistmakerfinish.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmakerfinish.domain.SearchHistoryRepository.SearchHistoryRepository
import com.practicum.playlistmakerfinish.domain.model.Track

class SharedPreferencesSearchHistoryRepository(private val sharedPreferences: SharedPreferences) :
    SearchHistoryRepository {

    override fun saveTrack(track: Track) {
        val searchHistory = readTracks().toMutableList()

        searchHistory.removeAll { it.id == track.id }
        searchHistory.add(0, track)

        if (searchHistory.size > MAX_SIZE) {
            searchHistory.removeAt(MAX_SIZE)
        }

        val json = Gson().toJson(searchHistory)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    override fun readTracks(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        val arrayType = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, arrayType)
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