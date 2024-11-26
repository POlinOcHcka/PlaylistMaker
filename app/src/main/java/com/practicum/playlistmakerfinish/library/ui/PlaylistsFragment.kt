package com.practicum.playlistmakerfinish.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmakerfinish.library.domain.model.IntentKeys.PLAYLIST_ID_KEY
import com.practicum.playlistmakerfinish.library.domain.model.Playlist
import com.practicum.playlistmakerfinish.library.presentation.PlaylistsViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private val playlistsViewModel: PlaylistsViewModel by viewModel<PlaylistsViewModel>()

    private var adapter: PlaylistAdapter? = null

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val gson: Gson by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        adapter = PlaylistAdapter { playlist -> openPlaylist(playlist) }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.newPlaylist.setOnClickListener {
            val action = LibraryFragmentDirections.actionLibraryFragmentToNewPlaylistFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.playlists)
            is PlaylistState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        with(binding) {
            newPlaylist.isVisible = true
            placeholderText.isVisible = true
            placeholderImage.isVisible = true
            recyclerView.isVisible = false
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        with(binding) {
            recyclerView.adapter?.notifyDataSetChanged()

            newPlaylist.isVisible = true
            placeholderText.isVisible = false
            placeholderImage.isVisible = false
            recyclerView.isVisible = true
        }

        adapter?.playlists?.clear()
        adapter?.playlists?.addAll(playlists)
        adapter?.notifyDataSetChanged()
    }

    private fun openPlaylist(playlist: Playlist) {
        val bundle = Bundle().apply {
            putString(PLAYLIST_ID_KEY, gson.toJson(playlist))
        }
        findNavController().navigate(R.id.action_libraryFragment_to_playlistViewFragment, bundle)
    }
}