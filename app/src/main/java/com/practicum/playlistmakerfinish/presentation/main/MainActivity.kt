package com.practicum.playlistmakerfinish.presentation.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.presentation.search.SearchActivity
import com.practicum.playlistmakerfinish.presentation.settings.SettingsActivity
import com.practicum.playlistmakerfinish.presentation.library.LibraryActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.button_search)

        buttonSearch.setOnClickListener {
            val buttonSearch = Intent(this, SearchActivity::class.java)
            startActivity(buttonSearch)
        }

        val buttonMedia = findViewById<Button>(R.id.button_media)

        buttonMedia.setOnClickListener {
            val buttonMedia = Intent(this, LibraryActivity::class.java)
            startActivity(buttonMedia)
        }

        val buttonSettings = findViewById<Button>(R.id.button_settings)

        buttonSettings.setOnClickListener {
            val buttonSettings = Intent(this, SettingsActivity::class.java)
            startActivity(buttonSettings)
        }
    }
}