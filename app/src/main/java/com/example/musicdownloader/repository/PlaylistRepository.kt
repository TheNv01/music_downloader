package com.example.musicdownloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicdownloader.database.dao.PlaylistDAO
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.model.Playlist

class PlaylistRepository(private val playlistDAO: PlaylistDAO) {

    var playlists: List<Playlist> = playlistDAO.getAllPlaylist()

    suspend fun insertPlaylist(playlist: Playlist) {
        playlistDAO.insertPlaylist(playlist)
    }

    suspend fun addMusicToPlaylist(playlist: Playlist){
        playlistDAO.insertMusicToPlaylist(playlist)
    }

    suspend fun getOnePlaylist(name: String): Playlist{
        return playlistDAO.getOnePlaylist(name)
    }

}