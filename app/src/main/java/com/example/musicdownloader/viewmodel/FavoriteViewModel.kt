package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.musicdownloader.model.Music

class FavoriteViewModel(application: Application) : BaseViewModel(application) {

    val musics: LiveData<List<Music>> = favoriteRepository.musics

}