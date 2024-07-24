package com.practicum.playlistmakerfinish.settings.data

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmakerfinish.app.ui.App
import com.practicum.playlistmakerfinish.settings.domain.SettingsRepository

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    override var isDarkTheme: Boolean
        get() = sharedPreferences.getBoolean("theme_key", false)
        set(value) {
            sharedPreferences.edit().putBoolean("theme_key", value).apply()
            (context.applicationContext as App).switchTheme(value)
        }

    override fun switchTheme(isDark: Boolean) {
        isDarkTheme = isDark
    }
}