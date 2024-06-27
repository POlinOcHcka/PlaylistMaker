package com.practicum.playlistmakerfinish.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val THEME_PREFERENCES = "theme_preferences"
const val THEME_KEY = "night_fact"

class App : Application() {

    private val sharedPrefs by lazy {
        getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
    }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        darkTheme = getThemeSharedPreferences()
        switchTheme(darkTheme)
    }

    private fun getThemeSharedPreferences(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }

    private fun saveThemeSharedPreferences() {
        sharedPrefs.edit()
            .putBoolean(THEME_KEY, darkTheme)
            .apply()
    }

    private fun appTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        appTheme(darkThemeEnabled)
        saveThemeSharedPreferences()
    }
}

