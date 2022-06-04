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
import com.example.musicdownloader.repository.FavoriteRepository
import com.example.musicdownloader.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistOnViewModel(application: Application) : AndroidViewModel(application) {

    val existingPlaylist: LiveData<List<Playlist>>
    val optionsDownloaded = ArrayList<Option>()
    private val playlistRepository: PlaylistRepository
    var id = MutableLiveData<Long>()
    private val favoriteRepository: FavoriteRepository
    val musics: LiveData<List<Music>>


    init {
        val playlistDAO =
            MusicRoomDatabase.MusicDatabaseBuilder.getInstance(application.applicationContext)
                .playlistDAO()
        playlistRepository = PlaylistRepository(playlistDAO)

        val favoriteDAO =
            MusicRoomDatabase.MusicDatabaseBuilder.getInstance(application.applicationContext)
                .favoriteDAO()
        favoriteRepository = FavoriteRepository(favoriteDAO)
        existingPlaylist = playlistRepository.playlists
        musics = favoriteRepository.musics
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