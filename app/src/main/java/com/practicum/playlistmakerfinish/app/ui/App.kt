package com.practicum.playlistmakerfinish.app.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmakerfinish.ServiceLocator.ServiceLocator
import com.practicum.playlistmakerfinish.app.domain.GetThemeUseCase
import com.practicum.playlistmakerfinish.app.domain.SetThemeUseCase

const val THEME_PREFERENCES = "theme_preferences"
const val THEME_KEY = "night_fact"

class App : Application() {
    private lateinit var getThemeUseCase: GetThemeUseCase
    private lateinit var setThemeUseCase: SetThemeUseCase

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        getThemeUseCase = ServiceLocator.provideGetThemeUseCase(this)
        setThemeUseCase = ServiceLocator.provideSetThemeUseCase(this)

        val darkTheme = getThemeUseCase.execute()
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        setThemeUseCase.execute(darkThemeEnabled)
    }
}