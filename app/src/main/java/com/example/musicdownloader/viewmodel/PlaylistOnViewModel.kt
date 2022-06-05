package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.R
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistOnViewModel(application: Application) : BaseViewModel(application) {

    val existingPlaylist: LiveData<List<Playlist>> = playlistRepository.playlists
    val optionsDownloaded = ArrayList<Option>()
    var id = MutableLiveData<Long>()
    val musics: LiveData<List<Music>> = favoriteRepository.musics


    init {
        initOption()
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            id.postValue(playlistRepository.insertPlaylist(playlist))
        }
    }

    private fun initOption(){
        optionsDownloaded.add(Option("Rename Playlist", R.drawable.ic_rename))
        optionsDownloaded.add(Option("Remove Playlist", R.drawable.ic_delete))

    }

    fun deletePlaylist(id: Int){
        playlistRepository.deletePlaylist(id)
    }

    fun renamePlaylist(name: String, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.renamePlaylist(name, id)
        }
    }

}