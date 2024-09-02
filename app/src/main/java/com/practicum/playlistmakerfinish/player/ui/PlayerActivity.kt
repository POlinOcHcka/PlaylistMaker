package com.practicum.playlistmakerfinish.player.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.player.domain.PlayerTrack
import com.practicum.playlistmakerfinish.player.presentation.PlayerViewModel
import com.practicum.playlistmakerfinish.search.domain.model.IntentKeys.SEARCH_HISTORY_KEY
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
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
    private lateinit var favoriteButton: ImageButton

    private companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer: MediaPlayer? = null
    private var url: String? = null

    private val viewModel by viewModel<PlayerViewModel>()
    private val mediaPlayerProvider by inject<MediaPlayer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

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
        favoriteButton = findViewById(R.id.button_like)

        back.setOnClickListener { finish() }
        play.setOnClickListener { playbackControl() }

        favoriteButton.setOnClickListener {
            viewModel.trackLiveData.value?.let { track ->
                viewModel.toggleFavorite(track)
            }
        }

        val value: String? = intent.getStringExtra(SEARCH_HISTORY_KEY)
        value?.let { viewModel.getTrack(it) }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.trackLiveData.observe(this) { track ->
            track?.let {
                if (url == null) {
                    updateUI(it)
                    url = it.previewUrl
                    preparePlayer()
                } else {
                    updateFavoriteButton(it.isFavorite)
                }
            }
        }

        viewModel.currentTimeLiveData.observe(this) { formattedTime ->
            playTime.text = formattedTime
        }

        viewModel.playerStateLiveData.observe(this) { state ->
            playerState = state
        }
    }

    private fun updateUI(track: PlayerTrack) {
        val artworkUrl = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg") ?: ""

        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.placeholder_player)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.size_16)))
            .into(albumCover)

        trackName.text = track.name
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.timeMillis)
        album.text = track.collectionName ?: ""
        year.text = track.releaseDate.split("-").getOrNull(0) ?: ""
        genre.text = track.primaryGenreName
        country.text = track.country
        play.setImageResource(R.drawable.icon_play)

        updateFavoriteButton(track.isFavorite)
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.icon_favorite_tracks)
        } else {
            favoriteButton.setImageResource(R.drawable.icon_like)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
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
        mediaPlayer = mediaPlayerProvider.apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                viewModel.setPlayerState(STATE_PREPARED)
            }
            setOnCompletionListener {
                viewModel.setPlayerState(STATE_PREPARED)
                viewModel.stopUpdatingProgress()
                playTime.text = String.format("%02d:%02d", 0, 0)
                play.setImageResource(R.drawable.icon_play)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        play.setImageResource(R.drawable.icon_pause)
        viewModel.setPlayerState(STATE_PLAYING)
        mediaPlayer?.let { viewModel.startUpdatingProgress(it) }
    }

    private fun pausePlayer() {
        mediaPlayer?.pause()
        play.setImageResource(R.drawable.icon_play)
        viewModel.setPlayerState(STATE_PAUSED)
        viewModel.stopUpdatingProgress()
    }
}