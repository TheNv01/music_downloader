package com.example.musicdownloader.repository

import androidx.lifecycle.LiveData
import com.example.musicdownloader.database.dao.PlaylistDAO
import com.example.musicdownloader.model.Playlist

class PlaylistRepository(private val playlistDAO: PlaylistDAO) {

    var playlists = playlistDAO.getAllPlaylist()


    suspend fun insertPlaylist(playlist: Playlist): Long {
        return playlistDAO.insertPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: Playlist){
        playlistDAO.updatePlaylist(playlist)
    }

    fun getListMusic(id: Int): Playlist{
        return playlistDAO.getOnePlaylist(id)
    }

    fun getPlaylistFlowName(name: String): LiveData<List<Playlist>> {
        return playlistDAO.getPlaylistFlowName(name)
    }

    suspend fun renamePlaylist(name: String, id: Int){
        playlistDAO.renamePlaylist(name, id)
    }

    fun deletePlaylist(id: Int){
        playlistDAO.deletePlaylist(id)
    }

}