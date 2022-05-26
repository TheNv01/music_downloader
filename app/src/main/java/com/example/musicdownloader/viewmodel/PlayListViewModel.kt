package com.example.musicdownloader.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.database.MusicRoomDatabase
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.model.Playlist
import com.example.musicdownloader.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayListViewModel(application: Application) : AndroidViewModel(application) {

    private var _existingPlaylist = MutableLiveData<List<Playlist>>()
    val existingPlaylist: LiveData<List<Playlist>> = _existingPlaylist

    private val repository: PlaylistRepository

    init {
        val playlistDAO =
            MusicRoomDatabase.MusicDatabaseBuilder.getInstance(application.applicationContext)
                .playlistDAO()
        repository = PlaylistRepository(playlistDAO)
        getExistingPlaylist()

    }

    fun getExistingPlaylist() {
        _existingPlaylist.value = repository.playlists
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPlaylist(playlist)
        }
    }

    fun addMusicToPlaylist(namePlaylist: String, music: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            val listMusic = repository.getOnePlaylist(namePlaylist).musics
            listMusic.add(music)
            repository.addMusicToPlaylist(Playlist(namePlaylist, listMusic))
        }
    }
}