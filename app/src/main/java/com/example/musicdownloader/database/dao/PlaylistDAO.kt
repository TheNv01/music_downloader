package com.example.musicdownloader.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.musicdownloader.model.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDAO {
    @Insert
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Query("select * from playlist")
    fun getAllPlaylist(): LiveData<List<Playlist>>

    @Query("select * from playlist where id = :id")
    fun getOnePlaylist(id: Int): Playlist

    @Query("select * from playlist where name like :name")
    fun getPlaylistFlowName(name: String): LiveData<List<Playlist>>

    @Query("UPDATE playlist SET name = :name WHERE id = :id")
    suspend fun renamePlaylist(name: String, id: Int)

    @Query("DELETE FROM playlist WHERE id = :id")
    fun deletePlaylist(id: Int)

}