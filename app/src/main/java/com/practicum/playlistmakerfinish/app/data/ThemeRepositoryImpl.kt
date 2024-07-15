package com.practicum.playlistmakerfinish.app.data

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmakerfinish.app.domain.ThemeRepository

class ThemeRepositoryImpl(context: Context) : ThemeRepository {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)

    override fun isDarkTheme(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }

    override fun setDarkTheme(isDark: Boolean) {
        sharedPrefs.edit().putBoolean(THEME_KEY, isDark).apply()
    }

    companion object {
        private const val THEME_PREFERENCES = "theme_prefs"
        private const val THEME_KEY = "theme_key"
    }
}