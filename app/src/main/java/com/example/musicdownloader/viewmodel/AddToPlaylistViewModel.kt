package com.example.musicdownloader.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.database.MusicRoomDatabase
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Playlist
import com.example.musicdownloader.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddToPlaylistViewModel(application: Application): AndroidViewModel(application) {

    var existingPlaylist: LiveData<List<Playlist>>

    private val repository: PlaylistRepository

    init {
        val playlistDAO =
            MusicRoomDatabase.MusicDatabaseBuilder.getInstance(application.applicationContext)
                .playlistDAO()
        repository = PlaylistRepository(playlistDAO)
        existingPlaylist = getPlaylistFlowName("%%")
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPlaylist(playlist)
        }
    }

    fun getPlaylistFlowName(name: String): LiveData<List<Playlist>>{
        return repository.getPlaylistFlowName(name)
    }

    fun addMusicToPlaylist(name: String, id: Int, music: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            val musics = repository.getListMusic(id).musics
            if (!isInsideList(music, musics)){
                musics.add(music)
                repository.updatePlaylist(Playlist(name, musics, id))
            }
            else{
                return@launch
            }
        }
    }

    private fun isInsideList(music: Music, musicList: List<Music>): Boolean{
        for(i in musicList.indices){
            if(music.id == musicList[i].id){
                return true
            }
        }
        return false
    }
}