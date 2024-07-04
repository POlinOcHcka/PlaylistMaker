package com.practicum.playlistmakerfinish.settings.domain

import com.practicum.playlistmakerfinish.settings.data.SettingsRepository

class SwitchThemeUseCase(private val settingsRepository: SettingsRepository) {
    fun switchTheme(isDark: Boolean) {
        settingsRepository.switchTheme(isDark)
    }

    fun isDarkTheme(): Boolean {
        return settingsRepository.isDarkTheme
    }
}