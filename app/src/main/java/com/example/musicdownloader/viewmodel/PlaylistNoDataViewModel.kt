package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.R
import com.example.musicdownloader.model.Option
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistNoDataViewModel(application: Application) : BaseViewModel(application) {

    val optionsDownloaded = ArrayList<Option>()

    init {
        initOption()
    }

    private fun initOption(){
        optionsDownloaded.add(Option("Rename Playlist", R.drawable.ic_rename))
        optionsDownloaded.add(Option("Remove Playlist", R.drawable.ic_delete))

    }

    fun deletePlaylist(id: Int){
        playlistRepository.deletePlaylist(id)
    }

    fun renamePlaylist(name: String, id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.renamePlaylist(name, id)
        }
    }
}