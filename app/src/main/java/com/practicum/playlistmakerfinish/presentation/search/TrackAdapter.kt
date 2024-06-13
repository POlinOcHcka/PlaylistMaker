package com.practicum.playlistmakerfinish.presentation.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.domain.model.Track
import java.text.SimpleDateFormat
import java.util.*

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private val trackList: MutableList<Track> = mutableListOf()

    var onTrackClickListener: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track_layout, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        if (position in trackList.indices) {
            holder.bind(trackList[position])
            holder.itemView.setOnClickListener {
                Log.d("TrackAdapter", "Track clicked: ${trackList[position]}")
                onTrackClickListener?.invoke(trackList[position])}
        }
    }

    fun setList(list: MutableList<Track>) {
        trackList.clear()
        trackList.addAll(list)
        notifyDataSetChanged()
    }

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val artistName: TextView = itemView.findViewById(R.id.artist_name)
        private val trackTime: TextView = itemView.findViewById(R.id.track_time)
        private val url: ImageView = itemView.findViewById(R.id.artworkUrl100)

        fun bind(track: Track) {
            trackName.text = track.name
            artistName.text = track.artistName
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.timeMillis)

            Glide
                .with(itemView)
                .load(track.artworkUrl100)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(url)
        }
    }
}