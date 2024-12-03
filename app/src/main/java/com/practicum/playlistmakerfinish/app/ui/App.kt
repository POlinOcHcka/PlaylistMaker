package com.practicum.playlistmakerfinish.app.ui

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmakerfinish.app.di.appModule
import com.practicum.playlistmakerfinish.app.presentation.AppViewModel
import com.practicum.playlistmakerfinish.library.di.*
import com.practicum.playlistmakerfinish.player.di.playerModule
import com.practicum.playlistmakerfinish.search.di.searchModule
import com.practicum.playlistmakerfinish.settings.di.settingsModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        applySavedTheme()

        startKoin {
            androidContext(this@App)
            modules(appModule, playerModule, searchModule, settingsModule, libraryModule, dataModule, repositoryModule, presentationModule, playlistsModule, newPlaylistsModule, interactorModule, editPlaylistModule)
        }
    }

    private fun applySavedTheme() {
        val sharedPreferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("theme_key", false)
        switchTheme(isDarkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}