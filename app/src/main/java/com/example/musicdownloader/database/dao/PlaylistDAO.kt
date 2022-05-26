package com.example.musicdownloader.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.model.Playlist

@Dao
interface PlaylistDAO {
    @Insert
    suspend fun insertPlaylist(playlist: Playlist)

    @Update
    suspend fun insertMusicToPlaylist(playlist: Playlist)

    @Query("select * from playlist")
    fun getAllPlaylist(): List<Playlist>

    @Query("select * from playlist where name = :name")
    suspend fun getOnePlaylist(name: String): Playlist

}