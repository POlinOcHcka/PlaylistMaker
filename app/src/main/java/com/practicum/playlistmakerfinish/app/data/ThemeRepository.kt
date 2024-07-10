package com.practicum.playlistmakerfinish.app.data

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(isDark: Boolean)
}