package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.database.MusicRoomDatabase
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.networking.Services
import com.example.musicdownloader.repository.PlaylistRepository
import kotlinx.coroutines.launch

class PlaylistInsideViewModel(application: Application): AndroidViewModel(application) {

    private var _musics = MutableLiveData<List<Music>>()
    val musics: LiveData<List<Music>> = _musics

    private val repository: PlaylistRepository

    init {
        val playlistDAO = MusicRoomDatabase.MusicDatabaseBuilder.getInstance(application.applicationContext).playlistDAO()
        repository = PlaylistRepository(playlistDAO)

    }

    fun  getMusics(namePlaylist: String){
        viewModelScope.launch {
            _musics.value = repository.getOnePlaylist(namePlaylist).musics
        }
    }
}