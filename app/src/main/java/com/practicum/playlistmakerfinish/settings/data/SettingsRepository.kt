package com.practicum.playlistmakerfinish.settings.data

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmakerfinish.App

class SettingsRepository(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var isDarkTheme: Boolean
        get() = sharedPreferences.getBoolean("dark_theme", false)
        set(value) {
            sharedPreferences.edit().putBoolean("dark_theme", value).apply()
        }

    fun switchTheme(isDark: Boolean) {
        isDarkTheme = isDark
        (context.applicationContext as App).switchTheme(isDark)
    }
}