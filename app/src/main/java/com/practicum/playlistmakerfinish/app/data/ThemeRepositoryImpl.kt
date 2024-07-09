package com.practicum.playlistmakerfinish.app.data

import android.content.Context
import com.practicum.playlistmakerfinish.app.ui.THEME_KEY
import com.practicum.playlistmakerfinish.app.ui.THEME_PREFERENCES

class ThemeRepositoryImpl(context: Context) : ThemeRepository {
    private val sharedPrefs = context.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)

    override fun isDarkTheme(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }

    override fun setDarkTheme(isDark: Boolean) {
        sharedPrefs.edit()
            .putBoolean(THEME_KEY, isDark)
            .apply()
    }
}