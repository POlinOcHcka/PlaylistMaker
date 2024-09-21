package com.practicum.playlistmakerfinish.library.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.databinding.PlaylistViewBinding
import com.practicum.playlistmakerfinish.library.domain.model.Playlist
import com.practicum.playlistmakerfinish.search.domain.model.TrackWord

class PlaylistViewHolder(private val binding: PlaylistViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        with(binding) {
            playlistName.text = playlist.playlistName
            tracksCount.text = playlist.tracksCount.toString()+" "+TrackWord.getTrackWord(playlist.tracksCount, itemView)
        }
        Glide.with(itemView).load(playlist.uri).centerCrop()
            .placeholder(R.drawable.placeholder).into(binding.playlistCover)
    }
}