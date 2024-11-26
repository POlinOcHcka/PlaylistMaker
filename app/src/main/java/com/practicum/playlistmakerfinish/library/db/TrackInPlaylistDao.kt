package com.practicum.playlistmakerfinish.library.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM track_in_playlist_table WHERE trackId IN (:trackIds)")
    fun getTracksInPlaylist(trackIds: List<Int>): Flow<List<TrackInPlaylistEntity>>

    @Query("DELETE FROM track_in_playlist_table WHERE trackId = :trackId")
    fun removeTrack(trackId: Int)
}