package com.practicum.playlistmakerfinish.player.ui

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.player.domain.model.PlayerTrack
import com.practicum.playlistmakerfinish.player.domain.model.toTrack
import com.practicum.playlistmakerfinish.player.presentation.PlayerViewModel
import com.practicum.playlistmakerfinish.search.domain.model.IntentKeys.SEARCH_HISTORY_KEY
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

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
    private lateinit var buttonAddTrack: ImageButton
    private lateinit var overlay: View
    private lateinit var newPlaylistButton: Button
    private lateinit var screen: CoordinatorLayout
    private lateinit var bottomSheet: LinearLayout

    private companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TAG = "PlayerFragment"
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer: MediaPlayer? = null
    private var url: String? = null

    private val viewModel by viewModel<PlayerViewModel>()
    private val mediaPlayerProvider by inject<MediaPlayer>()

    private var adapter: BottomSheetPlaylistAdapter? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back = view.findViewById(R.id.button_back_player)
        albumCover = view.findViewById(R.id.album_cover)
        trackName = view.findViewById(R.id.track_name)
        artistName = view.findViewById(R.id.artist_name)
        trackTime = view.findViewById(R.id.track_time)
        album = view.findViewById(R.id.track_album)
        year = view.findViewById(R.id.release_date)
        genre = view.findViewById(R.id.track_genre)
        country = view.findViewById(R.id.track_country)
        play = view.findViewById(R.id.button_play)
        playTime = view.findViewById(R.id.play_time)
        favoriteButton = view.findViewById(R.id.button_like)
        buttonAddTrack = view.findViewById(R.id.button_add_track)
        overlay = view.findViewById(R.id.overlay)
        newPlaylistButton = view.findViewById(R.id.newPlaylist)
        bottomSheet = view.findViewById(R.id.playlistsBottomSheet)
        screen = view.findViewById(R.id.screen)

        overlay.visibility = View.GONE

        back.setOnClickListener { activity?.onBackPressed() }
        play.setOnClickListener { playbackControl() }

        favoriteButton.setOnClickListener {
            viewModel.trackLiveData.value?.let { track ->
                viewModel.toggleFavorite(track)
            }
        }

        val value: String? = arguments?.getString(SEARCH_HISTORY_KEY)
        value?.let { viewModel.getTrack(it) }

        observeViewModel()
        setupBottomSheet()

        buttonAddTrack.setOnClickListener {
            showBottomSheet()
            viewModel.getSavedPlaylists()
        }

        overlay.setOnClickListener {
            hideBottomSheet()
        }

        newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }
        play.setImageResource(R.drawable.icon_play)
    }

    override fun onResume() {
        super.onResume()
        viewModel.trackLiveData.value?.let {
            updateUI(it)
        }

        if (playerState == STATE_PLAYING || playerState == STATE_PAUSED) {
            mediaPlayer?.let { viewModel.startUpdatingProgress(it) }
        }
    }

    private fun observeViewModel() {
        viewModel.trackLiveData.observe(viewLifecycleOwner) { track ->
            track?.let {
                if (url == null) {
                    updateUI(it)
                    url = it.previewUrl
                    preparePlayer()
                } else {
                    updateUI(it)
                    updateFavoriteButton(it.isFavorite)
                }
            }
        }

        viewModel.currentTimeLiveData.observe(viewLifecycleOwner) { formattedTime ->
            playTime.text = formattedTime
        }

        viewModel.playerStateLiveData.observe(viewLifecycleOwner) { state ->
            playerState = state
        }

        viewModel.observeTrackInPlaylistState().observe(viewLifecycleOwner) { state ->
            makeToast(state)
        }
    }

    private fun updateUI(track: PlayerTrack) {
        val artworkUrl =
            track.artworkUrl100.takeIf { it.isNotBlank() }?.replaceAfterLast('/', "512x512bb.jpg")
                ?: ""

        Glide.with(this)
            .load(artworkUrl.takeIf { it.isNotBlank() } ?: R.drawable.placeholder_player)
            .placeholder(R.drawable.placeholder_player).error(R.drawable.placeholder_player)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.size_16)))
            .into(albumCover)

        trackName.text = track.name
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.timeMillis)
        album.text = track.collectionName ?: ""
        year.text = track.releaseDate.split("-").getOrNull(0) ?: ""
        genre.text = track.primaryGenreName
        country.text = track.country

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

    override fun onDestroyView() {
        super.onDestroyView()
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

    private fun showBottomSheet() {
        overlay.visibility = View.VISIBLE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideBottomSheet() {
        overlay.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupBottomSheet() {
        adapter = BottomSheetPlaylistAdapter().apply {
            itemClickListener = { _, tracksIdInPlaylist, playlist ->
                viewModel.trackLiveData.value?.let { track ->
                    viewModel.addTracksIdInPlaylist(playlist, tracksIdInPlaylist, track.toTrack())
                }
            }
        }

        bottomSheetBehavior =
            BottomSheetBehavior.from(requireView().findViewById(R.id.playlistsBottomSheet))
        requireView().findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    overlay.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        bottomSheet.viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                screen.let {
                    bottomSheetBehavior.peekHeight = screen.measuredHeight * 2/3
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) view?.viewTreeObserver?.removeOnGlobalLayoutListener(
                    this
                )
                else view?.viewTreeObserver?.removeGlobalOnLayoutListener(this)
            }
        })

        observePlaylists()
    }

    private fun observePlaylists() {
        viewModel.observePlaylists().observe(viewLifecycleOwner) { playlists ->
            if (!playlists.isNullOrEmpty()) {
                adapter?.playlists?.clear()
                adapter?.playlists?.addAll(playlists)
                adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun makeToast(state: TrackInPlaylistState) {
        when (state) {
            is TrackInPlaylistState.TrackIsAlreadyInPlaylist -> Toast.makeText(
                requireContext(),
                getString(R.string.track_added_to_playlist_already) + " ${state.playlistName}",
                Toast.LENGTH_SHORT
            ).show()

            is TrackInPlaylistState.TrackAddToPlaylist -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.track_added_now) + " ${state.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
                hideBottomSheet()
            }
        }
    }
}