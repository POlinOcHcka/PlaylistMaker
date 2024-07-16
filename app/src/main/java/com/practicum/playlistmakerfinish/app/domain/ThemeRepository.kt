package com.practicum.playlistmakerfinish.app.domain

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(isDark: Boolean)
}