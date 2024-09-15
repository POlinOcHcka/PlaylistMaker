package com.practicum.playlistmakerfinish.player.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.databinding.BottomSheetViewBinding
import com.practicum.playlistmakerfinish.library.domain.model.Playlist
import com.practicum.playlistmakerfinish.search.domain.model.TrackWord

class BottomSheetPlaylistViewHolder(private val binding: BottomSheetViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist, clickListener: ((Int, List<Int>, Playlist) -> Unit)?) {
        with(binding) {
            playlistName.text = playlist.playlistName
            tracksCount.text = playlist.tracksCount.toString() + " " +
                    TrackWord.getTrackWord(playlist.tracksCount, itemView)

            Glide.with(itemView)
                .load(playlist.uri.takeIf { !it.isNullOrBlank() } ?: R.drawable.placeholder_player)
                .placeholder(R.drawable.placeholder_player)
                .centerCrop()
                .into(playlistCover)
        }

        binding.root.setOnClickListener {
            clickListener?.invoke(adapterPosition, playlist.tracksIdInPlaylist, playlist)
        }
    }
}