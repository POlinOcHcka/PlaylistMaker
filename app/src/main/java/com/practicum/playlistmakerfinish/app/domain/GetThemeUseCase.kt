package com.practicum.playlistmakerfinish.app.domain

class GetThemeUseCase(private val themeRepository: ThemeRepository) {
    fun execute(): Boolean {
        return themeRepository.isDarkTheme()
    }
}