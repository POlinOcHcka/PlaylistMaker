package com.practicum.playlistmakerfinish.settings.domain

class SwitchThemeUseCase(private val settingsRepository: SettingsRepository) {
    fun switchTheme(isDark: Boolean) {
        settingsRepository.switchTheme(isDark)
    }

    fun isDarkTheme(): Boolean {
        return settingsRepository.isDarkTheme
    }
}