package com.practicum.playlistmakerfinish.library.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.databinding.FragmentPlaylistDetailBinding
import com.practicum.playlistmakerfinish.library.domain.model.IntentKeys.PLAYLIST_ID_KEY
import com.practicum.playlistmakerfinish.library.domain.model.Playlist
import com.practicum.playlistmakerfinish.library.domain.model.Track
import com.practicum.playlistmakerfinish.library.presentation.ViewPlaylistViewModel
import com.practicum.playlistmakerfinish.player.domain.model.PlayerTrack
import com.practicum.playlistmakerfinish.search.domain.model.IntentKeys.SEARCH_HISTORY_KEY
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistViewFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ViewPlaylistViewModel>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetBehaviorMoreOptions: BottomSheetBehavior<LinearLayout>
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    lateinit var confirmDialogTrack: MaterialAlertDialogBuilder
    private val gson: Gson by inject()
    private lateinit var currentPlaylist: String

    companion object {
        fun newInstance() = PlaylistViewFragment()
        private const val TAG = "PlaylistViewFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.overlay.visibility = View.GONE
        val adapter =
            PlaylistTracksAdapter(onTrackClick = { track: Track -> openPlayerWithTrack(track) },
                onLongTrackClick = { track: Track ->
                    confirmDialogTrack =
                        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.remove_track_question))
                            .setMessage(R.string.remove_track_question)
                            .setPositiveButton(getString(R.string.yes_option)) { _, _ ->
                                viewModel.removeTrack(track.id)
                                currentPlaylist.let { viewModel.getPlaylist(currentPlaylist) }
                            }.setNegativeButton(getString(R.string.no_option)) { _, _ ->
                            }
                    confirmDialogTrack.show()
                    true
                })
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        val shareClickListener = View.OnClickListener { sharePlaylist() }
        binding.buttonShare.setOnClickListener(shareClickListener)

        binding.buttonBackPlaylist.setOnClickListener { activity?.onBackPressed() }

        viewModel.playlistLiveData.observe(viewLifecycleOwner) { playlist ->
            binding.playlistName.text = playlist.playlistName
            if (playlist.playlistDescription.isNullOrEmpty()) {
                binding.playlistDescription.visibility = View.GONE
            } else {
                binding.playlistDescription.visibility = View.VISIBLE
                binding.playlistDescription.text = playlist.playlistDescription
            }
            Glide.with(requireContext()).load(playlist.uri).placeholder(R.drawable.placeholder)
                .diskCacheStrategy(
                    DiskCacheStrategy.NONE
                ).skipMemoryCache(true).into(binding.playlistImage)
            Glide.with(requireContext()).load(playlist.uri).placeholder(R.drawable.placeholder)
                .diskCacheStrategy(
                    DiskCacheStrategy.NONE
                ).skipMemoryCache(true).into(binding.playlistImageBottom)
            binding.playlistNameBottom.text = playlist.playlistName

            confirmDialog =
                MaterialAlertDialogBuilder(requireContext()).setMessage("Хотите удалить плейлист \"${playlist.playlistName}\"?")
                    .setNeutralButton("Нет") { _, _ -> }.setPositiveButton("Да") { _, _ ->
                        viewModel.deletePlaylist()
                        findNavController().navigateUp()
                    }
            binding.editPlaylistButton.setOnClickListener {
                openPlaylist(playlist)
            }
        }
        viewModel.trackTimeLiveData.observe(viewLifecycleOwner) {
            binding.tracksTime.text = resources.getQuantityString(R.plurals.minutes, it.toInt(), it)
        }
        viewModel.trackListLiveData.observe(viewLifecycleOwner) {
            adapter.replaceTracks(it)
            binding.trackQuantityBottom.text = resources.getQuantityString(
                R.plurals.tracks, it.size, it.size
            )
            binding.tracksQty.text = resources.getQuantityString(
                R.plurals.tracks, it.size, it.size
            )
        }


        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet)
            .apply { state = BottomSheetBehavior.STATE_COLLAPSED }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> bottomSheetBehavior.state =
                        BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        binding.playlistBottomSheet.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val location = IntArray(2)
                binding.buttonShare.getLocationOnScreen(location)
                bottomSheetBehavior.peekHeight =
                    view.height - (location[1] + binding.buttonShare.measuredHeight)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) view.viewTreeObserver.removeOnGlobalLayoutListener(
                    this
                )
                else view.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        })
        bottomSheetBehaviorMoreOptions = BottomSheetBehavior.from(binding.moreOptionsBottom)
            .apply { state = BottomSheetBehavior.STATE_HIDDEN }

        binding.buttonMore.setOnClickListener {
            bottomSheetBehaviorMoreOptions.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.buttonShareBottom.setOnClickListener {
            sharePlaylist()
        }

        binding.removePlaylistButton.setOnClickListener {
            confirmDialog.show()
        }

        bottomSheetBehaviorMoreOptions.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.visibility = View.GONE
                    else -> binding.overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        val value = arguments?.getString(PLAYLIST_ID_KEY)
        value?.let {
            viewModel.getPlaylist(it)
            currentPlaylist = it
        }
    }

    private fun sharePlaylist() {
        val message = viewModel.getMessage()
        if (message == null) {
            Toast.makeText(
                requireContext(), getString(R.string.dont_share), Toast.LENGTH_SHORT
            ).show()
            return
        }
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться плейлистом"))
    }

    private fun openPlayerWithTrack(track: Track) {
        val playerTrack = PlayerTrack(
            id = track.id.toString(),
            artworkUrl100 = track.artworkUrl100,
            name = track.name,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            timeMillis = track.timeMillis,
            previewUrl = track.previewUrl
        )

        val bundle = Bundle().apply {
            putString(SEARCH_HISTORY_KEY, Gson().toJson(playerTrack))
        }

        findNavController().navigate(R.id.action_playlistViewFragment_to_playerFragment, bundle)
    }

    private fun openPlaylist(playlist: Playlist) {
        val bundle = Bundle().apply {
            putString(PLAYLIST_ID_KEY, gson.toJson(playlist))
        }
        findNavController().navigate(
            R.id.action_playlistViewFragment_to_editPlaylistFragment, bundle
        )
    }
}