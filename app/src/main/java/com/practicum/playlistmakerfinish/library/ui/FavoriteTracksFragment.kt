package com.practicum.playlistmakerfinish.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.library.db.TrackEntity
import com.practicum.playlistmakerfinish.library.presentation.FavoriteTracksViewModel
import com.practicum.playlistmakerfinish.player.domain.model.PlayerTrack
import com.practicum.playlistmakerfinish.search.domain.model.IntentKeys.SEARCH_HISTORY_KEY
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(): FavoriteTracksFragment {
            return FavoriteTracksFragment()
        }
    }

    private val viewModel by viewModel<FavoriteTracksViewModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteTracksAdapter
    private lateinit var placeholderImage: View
    private lateinit var placeholderText: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_tracks, container, false)
        recyclerView = view.findViewById(R.id.rvTrackFavorite)
        placeholderImage = view.findViewById(R.id.placeholderImage)
        placeholderText = view.findViewById(R.id.placeholderText)

        adapter = FavoriteTracksAdapter { track -> openPlayerWithTrack(track) }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.favoriteTracksLiveData.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                placeholderImage.visibility = View.VISIBLE
                placeholderText.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                placeholderImage.visibility = View.GONE
                placeholderText.visibility = View.GONE
                adapter.setTracks(tracks)
            }
        }
    }

    private fun openPlayerWithTrack(track: TrackEntity) {
        val playerTrack = PlayerTrack(
            id = track.trackId,
            artworkUrl100 = track.coverUrl,
            name = track.trackName,
            artistName = track.artistName,
            collectionName = track.albumName,
            releaseDate = track.releaseYear,
            primaryGenreName = track.genre,
            country = track.country,
            timeMillis = track.duration.toLong(),
            previewUrl = track.trackUrl
        )

        val bundle = Bundle().apply {
            putString(SEARCH_HISTORY_KEY, Gson().toJson(playerTrack))
        }

        findNavController().navigate(R.id.action_favoriteTracksFragment_to_playerFragment, bundle)
    }
}