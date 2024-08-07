package com.practicum.playlistmakerfinish.app.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmakerfinish.app.domain.GetThemeUseCase
import com.practicum.playlistmakerfinish.app.domain.SetThemeUseCase
import kotlinx.coroutines.launch

class AppViewModel(
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase
) : ViewModel() {

    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> get() = _isDarkTheme

    init {
        loadTheme()
    }

    fun loadTheme() {
        _isDarkTheme.value = getThemeUseCase.execute()
    }

    fun switchTheme(isDark: Boolean) {
        setThemeUseCase.execute(isDark)
        _isDarkTheme.value = isDark
    }

    fun getThemeState(): Boolean {
        return getThemeUseCase.execute()
    }
}