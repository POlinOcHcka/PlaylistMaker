package com.practicum.playlistmakerfinish.settings.data

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmakerfinish.app.ui.App

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override var isDarkTheme: Boolean
        get() = sharedPreferences.getBoolean("dark_theme", false)
        set(value) {
            sharedPreferences.edit().putBoolean("dark_theme", value).apply()
        }

    override fun switchTheme(isDark: Boolean) {
        isDarkTheme = isDark
        (context.applicationContext as App).switchTheme(isDark)
    }
}