package com.practicum.playlistmakerfinish.settings.data

interface SettingsRepository {
    var isDarkTheme: Boolean
    fun switchTheme(isDark: Boolean)
}