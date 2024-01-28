package com.practicum.playlistmakerfinish.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmakerfinish.R
import com.practicum.playlistmakerfinish.model.TrackModel
import java.text.SimpleDateFormat
import java.util.*

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var trackList = arrayListOf<TrackModel>()

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val artistName: TextView = itemView.findViewById(R.id.artist_name)
        private val trackTime: TextView = itemView.findViewById(R.id.track_time)
        private val url: ImageView = itemView.findViewById(R.id.artworkUrl100)

        fun bind(track: TrackModel) {

            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)

            Glide
                .with(itemView)
                .load(track.artworkUrl100)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(url)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_track_layout, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    fun setList(list: List<TrackModel>) {
        trackList.clear()
        trackList.addAll(list)
        this.notifyDataSetChanged()
    }
}