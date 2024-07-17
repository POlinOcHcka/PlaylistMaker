package com.practicum.playlistmakerfinish.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmakerfinish.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmakerfinish.library.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class Playlists : Fragment() {

    companion object {
        fun newInstance() = Playlists()
    }

    private val viewModel: PlaylistsViewModel by viewModel()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }
}