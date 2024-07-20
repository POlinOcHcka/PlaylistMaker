package com.practicum.playlistmakerfinish.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmakerfinish.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmakerfinish.library.presentation.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracks : Fragment() {

    companion object {
        fun newInstance() = FavoriteTracks()
    }

    private val viewModel: FavoriteTracksViewModel by viewModel()

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }
}