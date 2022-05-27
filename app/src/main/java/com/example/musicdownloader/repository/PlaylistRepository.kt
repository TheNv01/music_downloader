package com.example.musicdownloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicdownloader.database.dao.PlaylistDAO
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val playlistDAO: PlaylistDAO) {

    var playlists = playlistDAO.getAllPlaylist()

    suspend fun insertPlaylist(playlist: Playlist) {
        playlistDAO.insertPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: Playlist){
        playlistDAO.updatePlaylist(playlist)
    }

    fun getListMusic(id: Int): Playlist{
        return playlistDAO.getOnePlaylist(id)
    }

    suspend fun renamePlaylist(name: String, id: Int){
        playlistDAO.renamePlaylist(name, id)
    }

    fun deletePlaylist(id: Int){
        playlistDAO.deletePlaylist(id)
    }

}