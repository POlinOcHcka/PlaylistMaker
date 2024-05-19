package com.practicum.playlistmakerfinish

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
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

class PlayerActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var albumCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var play: ImageButton
    private lateinit var playTime: TextView

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val LAG = 500L
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer: MediaPlayer? = null
    private var mainThreadHandler: Handler? = null
    private var url: String? = null

    private val runnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.currentPosition)
                playTime.text = formattedTime
                mainThreadHandler?.postDelayed(this, LAG)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        mainThreadHandler = Handler(Looper.getMainLooper())

        back = findViewById(R.id.button_back_player)
        albumCover = findViewById(R.id.album_cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        trackTime = findViewById(R.id.track_time)
        album = findViewById(R.id.track_album)
        year = findViewById(R.id.release_date)
        genre = findViewById(R.id.track_genre)
        country = findViewById(R.id.track_country)
        play = findViewById(R.id.button_play)
        playTime = findViewById(R.id.play_time)

        back.setOnClickListener {
            finish()
        }

        play.setOnClickListener {
            playbackControl()
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
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)
            album.text = it.collectionName.takeIf { name -> !name.isNullOrEmpty() } ?: ""
            year.text = try { it.releaseDate.split("-", limit = 2)[0] } catch (e: Exception) { "" }
            genre.text = it.primaryGenreName
            country.text = it.country
            url = track.previewUrl
            play.setImageResource(R.drawable.icon_play)
            preparePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        mainThreadHandler?.removeCallbacks(runnable)
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PAUSED, STATE_PREPARED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        playTime.text = String.format("%02d:%02d", 0, 0)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            setOnCompletionListener {
                playerState = STATE_PREPARED

                mainThreadHandler?.removeCallbacks(runnable)
                playTime.text = String.format("%02d:%02d", 0, 0)

                play.setImageResource(R.drawable.icon_play)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        play.setImageResource(R.drawable.icon_pause)
        playerState = STATE_PLAYING

        mainThreadHandler?.postDelayed(runnable, LAG)
    }

    private fun pausePlayer() {
        mediaPlayer?.pause()
        play.setImageResource(R.drawable.icon_play)
        playerState = STATE_PAUSED

        mainThreadHandler?.removeCallbacks(runnable)
    }
}