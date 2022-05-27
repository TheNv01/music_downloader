package com.example.musicdownloader.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlayListViewModel(application: Application) : AndroidViewModel(application) {

    val existingPlaylist: LiveData<List<Playlist>>

    private var _isExist = MutableLiveData<Boolean>()
    val isExist : LiveData<Boolean> = _isExist

    private val repository: PlaylistRepository

    init {
        val playlistDAO =
            MusicRoomDatabase.MusicDatabaseBuilder.getInstance(application.applicationContext)
                .playlistDAO()
        repository = PlaylistRepository(playlistDAO)
        existingPlaylist = repository.playlists
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPlaylist(playlist)
        }
    }

    fun addMusicToPlaylist(name: String, id: Int, music: Music) {

        viewModelScope.launch(Dispatchers.IO) {
            val musics = repository.getListMusic(id).musics
            if(music in musics){
                _isExist.postValue(true)
                return@launch
            }
            else{
                musics.add(music)
                repository.updatePlaylist(Playlist(name, musics, id))
            }

        }
    }
}