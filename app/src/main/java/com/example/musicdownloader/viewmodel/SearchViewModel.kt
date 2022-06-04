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
import com.example.musicdownloader.networking.Services
import com.example.musicdownloader.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(application: Application): AndroidViewModel(application)  {

    private var _musics = MutableLiveData<List<Music>>()
    val musics: LiveData<List<Music>> = _musics

    private var _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    private val repository: PlaylistRepository

    init {
        _status.value = ApiStatus.DONE
        val playlistDAO =
            MusicRoomDatabase.MusicDatabaseBuilder.getInstance(application.applicationContext)
                .playlistDAO()
        repository = PlaylistRepository(playlistDAO)
    }

    fun addMusicToPlaylist(name: String, id: Int, musicList: List<Music>) {
        viewModelScope.launch(Dispatchers.IO) {
            val musics = repository.getListMusic(id).musics
            musicList.forEach {
                if(!isInsideList(it, musics)){
                    musics.add(it)
                    repository.updatePlaylist(Playlist(name, musics, id))
                }
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


    fun  getMusics(musics: List<Music>, keyword: String?, offset: Int = 0){
        _status.value = ApiStatus.LOADING
        viewModelScope.launch {
            try{
                val musicsFromApi = ArrayList<Music>()
                if(keyword == null){
                    Services.retrofitService.searchMusic( offset).let {
                        _musics.value = it.data
                    }
                    _status.value = ApiStatus.DONE
                }
                else{
                    Services.retrofitService.searchMusic(offset, keyword).let {
                        musicsFromApi.addAll(it.data)
                        musicsFromApi.forEach {music ->
                            musics.forEach { it2->
                                if(music.id == it2.id){
                                    music.isAddToPlaylist = true
                                }
                            }
                        }
                    }
                    _musics.value = musicsFromApi
                    _status.value = ApiStatus.DONE
                }
            } catch (e: Exception){
                _musics.value = listOf()
            }
        }
    }
}