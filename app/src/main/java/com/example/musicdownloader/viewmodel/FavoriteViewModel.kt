package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.musicdownloader.R
import com.example.musicdownloader.database.MusicRoomDatabase
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.repository.FavoriteRepository

class FavoriteViewModel(application: Application): AndroidViewModel(application) {

    private val repository: FavoriteRepository
    val musics: LiveData<List<Music>>

    init {
        val favoriteDAO = MusicRoomDatabase
            .MusicDatabaseBuilder
            .getInstance(application.applicationContext).favoriteDAO()
        repository = FavoriteRepository(favoriteDAO)
        musics = repository.musics
    }

}