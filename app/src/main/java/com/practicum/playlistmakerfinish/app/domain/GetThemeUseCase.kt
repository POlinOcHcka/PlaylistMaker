package com.practicum.playlistmakerfinish.app.domain

import com.practicum.playlistmakerfinish.app.data.ThemeRepository

class GetThemeUseCase(private val themeRepository: ThemeRepository) {
    fun execute(): Boolean {
        return themeRepository.isDarkTheme()
    }
}