package com.practicum.playlistmakerfinish.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmakerfinish.settings.domain.SwitchThemeUseCase

class SettingsViewModelFactory(private val switchThemeUseCase: SwitchThemeUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(switchThemeUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}