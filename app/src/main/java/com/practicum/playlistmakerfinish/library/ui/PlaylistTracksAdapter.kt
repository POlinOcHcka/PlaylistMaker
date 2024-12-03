package com.practicum.playlistmakerfinish.library.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.library.domain.model.Track
import java.text.SimpleDateFormat
import java.util.*

class PlaylistTracksAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onLongTrackClick: (Track) -> Boolean
) : RecyclerView.Adapter<PlaylistTracksAdapter.ViewHolder>() {

    private var tracks = listOf<Track>()

    fun replaceTracks(newTracks: List<Track>) {
        this.tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { onTrackClick(track) }
        holder.itemView.setOnLongClickListener { onLongTrackClick(track) }
    }

    override fun getItemCount() = tracks.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val artistName: TextView = itemView.findViewById(R.id.artist_name)
        private val trackTime: TextView = itemView.findViewById(R.id.track_time)
        private val coverImage: ImageView = itemView.findViewById(R.id.artworkUrl100)

        fun bind(track: Track) {
            trackName.text = track.name
            artistName.text = track.artistName

            val duration = track.timeMillis
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration)

            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .into(coverImage)
        }
    }
}