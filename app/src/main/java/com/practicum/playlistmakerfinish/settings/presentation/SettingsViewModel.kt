package com.practicum.playlistmakerfinish.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmakerfinish.settings.domain.SwitchThemeUseCase

class SettingsViewModel(private val switchThemeUseCase: SwitchThemeUseCase) : ViewModel() {

    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> get() = _isDarkTheme

    init {
        _isDarkTheme.value = switchThemeUseCase.isDarkTheme()
    }

    fun switchTheme(isDark: Boolean) {
        switchThemeUseCase.switchTheme(isDark)
        _isDarkTheme.value = isDark
    }
}