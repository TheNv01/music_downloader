package com.example.musicdownloader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.networking.Services
import kotlinx.coroutines.launch

class PlaylistInsideViewModel: BaseViewModel() {

    private var _musics = MutableLiveData<List<Music>>()
    val musics: LiveData<List<Music>> = _musics


    fun  getMusics(option: String, country: String?, offset: Int = 0,){
        viewModelScope.launch {
            try{
                if(country == null){
                    Services.retrofitService.getMusics(option, offset).let {
                        _musics.value = it.data.subList(0, 5)
                    }
                }
                else{
                    Services.retrofitService.getMusics(option, offset,country).let {
                        _musics.value = it.data.subList(0, 5)
                    }
                }

            } catch (e: Exception){
                _musics.value = listOf()
            }
        }
    }
}