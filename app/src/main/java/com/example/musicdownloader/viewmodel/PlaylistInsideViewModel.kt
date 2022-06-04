package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.R
import com.example.musicdownloader.database.MusicRoomDatabase
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.model.Playlist
import com.example.musicdownloader.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistInsideViewModel(application: Application): AndroidViewModel(application) {

    private var _musics = MutableLiveData<List<Music>>()
    val musics: LiveData<List<Music>> = _musics

    val existingPlaylist: LiveData<List<Playlist>>

    val optionsPlaylist = ArrayList<Option>()
    val optionsSong = ArrayList<Option>()

    private val repository: PlaylistRepository

    init {
        val playlistDAO = MusicRoomDatabase.MusicDatabaseBuilder.getInstance(application.applicationContext).playlistDAO()
        repository = PlaylistRepository(playlistDAO)
        existingPlaylist = repository.playlists
        initOption()
    }

    private fun initOption(){
        optionsPlaylist.add(Option("Rename playlist", R.drawable.ic_rename))
        optionsPlaylist.add(Option("Delete playlist", R.drawable.ic_delete))

        optionsSong.add(Option("Remove song from playlist", R.drawable.ic_delete))
        optionsSong.add(Option("Share", R.drawable.ic_share))
        optionsSong.add(Option("Set as ringtone", R.drawable.ic_bell))
        optionsSong.add(Option("Add to Favorite", R.drawable.ic_favorite))

    }


    fun getMusics(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            _musics.postValue(repository.getListMusic(id).musics)
        }
    }

    fun deletePlaylist(id: Int){
        repository.deletePlaylist(id)
    }

    fun removeMusic(name: String,id: Int, music: Music){
        viewModelScope.launch(Dispatchers.IO) {
            val musics = repository.getListMusic(id).musics
            musics.remove(music)
            repository.updatePlaylist(Playlist(name, musics, id))
        }
    }

    fun renamePlaylist(name: String, id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.renamePlaylist(name, id)
        }
    }
}