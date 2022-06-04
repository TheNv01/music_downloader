package com.example.musicdownloader.repository

import com.example.musicdownloader.database.dao.FavoriteDAO
import com.example.musicdownloader.model.Music

class FavoriteRepository (private val favoriteDAO: FavoriteDAO) {

    var musics = favoriteDAO.getFavorite()

    suspend fun insertMusicToFavorite(music: Music) {
        return favoriteDAO.insertPlaylist(music)
    }

    fun getListMusic(id: String): Music? {
        return favoriteDAO.getMusic(id)
    }

    fun deletePlaylist(id: String){
        favoriteDAO.deletePlaylist(id)
    }
}
