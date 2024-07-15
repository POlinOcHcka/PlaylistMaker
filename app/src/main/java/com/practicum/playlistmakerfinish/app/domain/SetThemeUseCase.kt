package com.practicum.playlistmakerfinish.app.domain

class SetThemeUseCase(private val themeRepository: ThemeRepository) {
    fun execute(isDark: Boolean) {
        themeRepository.setDarkTheme(isDark)
    }
}