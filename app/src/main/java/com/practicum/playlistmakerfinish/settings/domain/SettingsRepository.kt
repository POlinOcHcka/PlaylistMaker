package com.practicum.playlistmakerfinish.settings.domain

interface SettingsRepository {
    var isDarkTheme: Boolean
    fun switchTheme(isDark: Boolean)
}