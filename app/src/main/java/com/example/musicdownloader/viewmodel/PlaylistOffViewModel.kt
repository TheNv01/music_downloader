package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.R
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistOffViewModel(application: Application) : BaseViewModel(application) {
    private var _existingPlaylist = MutableLiveData<List<Playlist>>()
    val existingPlaylist: LiveData<List<Playlist>> = _existingPlaylist
    private val optionsDownloaded = ArrayList<Option>()

    init {

        _existingPlaylist.postValue(ArrayList())
        initOption()
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.insertPlaylist(playlist)
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