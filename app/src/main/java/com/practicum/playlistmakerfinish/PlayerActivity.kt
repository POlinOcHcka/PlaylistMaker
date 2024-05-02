package com.practicum.playlistmakerfinish

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.SharedPreferences.SEARCH_HISTORY_KEY
import com.practicum.playlistmakerfinish.model.TrackModel
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity: AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var albumCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var time: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        back = findViewById(R.id.button_back_player)
        albumCover = findViewById(R.id.album_cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        time = findViewById(R.id.track_time)
        album = findViewById(R.id.track_album)
        year = findViewById(R.id.releaseDate)
        genre = findViewById(R.id.trackGenre)
        country = findViewById(R.id.trackCountry)

        back.setOnClickListener {
            val backIntent = Intent(this, SearchActivity::class.java)
            startActivity(backIntent)
        }

        val value: String? = intent.getStringExtra(SEARCH_HISTORY_KEY)
        val track: TrackModel? = Gson().fromJson(value, TrackModel::class.java)

        track?.let {
            Glide.with(this)
                .load(it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.placeholder_player)
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.size_16)))
                .into(albumCover)
            trackName.text = it.trackName
            artistName.text = it.artistName
            time.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)
            album.text = it.collectionName.takeIf { name -> !name.isNullOrEmpty() } ?: ""
            year.text = try { it.releaseDate.split("-", limit = 2)[0] } catch (e: Exception) { "" }
            genre.text = it.primaryGenreName
            country.text = it.country
            Log.d("Player", "Show info")
        }
    }
}