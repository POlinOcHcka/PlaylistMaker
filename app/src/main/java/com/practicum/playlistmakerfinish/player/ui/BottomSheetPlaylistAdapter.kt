package com.practicum.playlistmakerfinish.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmakerfinish.databinding.BottomSheetViewBinding
import com.practicum.playlistmakerfinish.library.domain.model.Playlist


class BottomSheetPlaylistAdapter : RecyclerView.Adapter<BottomSheetPlaylistViewHolder>() {

    var itemClickListener: ((Int, List<Int>, Playlist) -> Unit)? = null

    val playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetPlaylistViewHolder {
        val binding = BottomSheetViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BottomSheetPlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: BottomSheetPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position], itemClickListener)
    }
}