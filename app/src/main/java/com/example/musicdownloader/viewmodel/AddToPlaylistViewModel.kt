package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddToPlaylistViewModel(application: Application): BaseViewModel(application) {

    var existingPlaylist: LiveData<List<Playlist>>

    init {

        existingPlaylist = getPlaylistFlowName("%%")
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.insertPlaylist(playlist)
        }
    }

    fun getPlaylistFlowName(name: String): LiveData<List<Playlist>>{
        return playlistRepository.getPlaylistFlowName(name)
    }

    fun addMusicToPlaylist(name: String, id: Int, music: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            val musics = playlistRepository.getListMusic(id).musics
            if (!isInsideList(music, musics)){
                musics.add(music)
                playlistRepository.updatePlaylist(Playlist(name, musics, id))
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