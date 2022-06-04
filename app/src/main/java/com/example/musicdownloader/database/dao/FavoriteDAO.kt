package com.example.musicdownloader.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.musicdownloader.model.Music

@Dao
interface FavoriteDAO {
    @Insert
    suspend fun insertPlaylist(music: Music)

    @Query("select * from favorite")
    fun getFavorite(): LiveData<List<Music>>

    @Query("select * from favorite where id = :id")
    fun getMusic(id: String): Music?

    @Query("DELETE FROM favorite WHERE id = :id")
    fun deletePlaylist(id: String)

}