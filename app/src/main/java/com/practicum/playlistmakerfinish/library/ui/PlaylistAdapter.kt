package com.practicum.playlistmakerfinish.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmakerfinish.databinding.PlaylistViewBinding
import com.practicum.playlistmakerfinish.library.domain.model.Playlist

class PlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit,
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    val playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding =
            PlaylistViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onPlaylistClick(playlist) }
    }
}