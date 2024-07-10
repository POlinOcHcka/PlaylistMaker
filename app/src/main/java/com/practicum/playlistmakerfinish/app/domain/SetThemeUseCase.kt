package com.practicum.playlistmakerfinish.app.domain

import com.practicum.playlistmakerfinish.app.data.ThemeRepository

class SetThemeUseCase(private val themeRepository: ThemeRepository) {
    fun execute(isDark: Boolean) {
        themeRepository.setDarkTheme(isDark)
    }
}