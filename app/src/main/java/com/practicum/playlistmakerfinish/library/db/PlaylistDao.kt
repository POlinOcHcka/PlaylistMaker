package com.practicum.playlistmakerfinish.library.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)
    @Query("SELECT * FROM playlist_table")
    fun getSavedPlaylists(): Flow<List<PlaylistEntity>>

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    fun getSavedPlaylist(playlistId: Int): Flow<PlaylistEntity>

    @Query("DELETE FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun removePlaylist(playlistId: Int)
}