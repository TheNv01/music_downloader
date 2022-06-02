package com.example.musicdownloader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.networking.Services
import kotlinx.coroutines.launch

class InsideGenresViewModel: BaseViewModel() {
    private var _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    private var _musics = MutableLiveData<List<Music>>()
    val musics: LiveData<List<Music>> = _musics

    fun  getMusics(genres: String, country: String?, offset: Int = 0,){
        viewModelScope.launch {
            try{
                if(country == null){
                    Services.retrofitService.getMusicsByGenres(genres, offset).let {
                        _musics.value = it.data
                    }
                    _status.value = ApiStatus.DONE
                }
                else{
                    Services.retrofitService.getMusicsByGenres(genres, offset,country).let {
                        _musics.value = it.data
                    }
                    _status.value = ApiStatus.DONE
                }

            } catch (e: Exception){
                _musics.value = listOf()
            }
        }
    }
}