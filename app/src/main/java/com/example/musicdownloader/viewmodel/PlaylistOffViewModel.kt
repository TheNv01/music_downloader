package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.R
import com.example.musicdownloader.database.MusicRoomDatabase
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.model.Playlist
import com.example.musicdownloader.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistOffViewModel(application: Application) : AndroidViewModel(application) {
    private var _existingPlaylist = MutableLiveData<List<Playlist>>()
    val existingPlaylist: LiveData<List<Playlist>> = _existingPlaylist
    val optionsDownloaded = ArrayList<Option>()
    private val repository: PlaylistRepository

    init {
        val playlistDAO =
            MusicRoomDatabase.MusicDatabaseBuilder.getInstance(application.applicationContext)
                .playlistDAO()
        repository = PlaylistRepository(playlistDAO)
        _existingPlaylist.postValue(ArrayList())
        initOption()
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPlaylist(playlist)
        }
    }

    private fun initOption(){
        optionsDownloaded.add(Option("Rename Playlist", R.drawable.ic_rename))
        optionsDownloaded.add(Option("Remove Playlist", R.drawable.ic_delete))

    }

    fun deletePlaylist(id: Int){
        repository.deletePlaylist(id)
    }

    fun renamePlaylist(name: String, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.renamePlaylist(name, id)
        }
    }
}