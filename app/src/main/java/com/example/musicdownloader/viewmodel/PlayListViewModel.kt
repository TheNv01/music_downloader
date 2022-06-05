package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayListViewModel(application: Application) : BaseViewModel(application) {

    val existingPlaylist: LiveData<List<Playlist>> = playlistRepository.playlists

    private var _isExist = MutableLiveData<Boolean>()
    val isExist : LiveData<Boolean> = _isExist

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.insertPlaylist(playlist)
        }
    }

    fun addMusicToPlaylist(name: String, id: Int, music: Music) {

        viewModelScope.launch(Dispatchers.IO) {
            val musics = playlistRepository.getListMusic(id).musics
            if(music in musics){
                _isExist.postValue(true)
                return@launch
            }
            else{
                musics.add(music)
                playlistRepository.updatePlaylist(Playlist(name, musics, id))
            }
        }
    }
}