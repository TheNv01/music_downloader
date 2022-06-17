package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.networking.Services
import kotlinx.coroutines.launch

class SeeAllViewModel(application: Application) : BaseViewModel(application){
    private var _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    private var _musics = MutableLiveData<List<Music>>()
    val musics: LiveData<List<Music>> = _musics

    val title = MutableLiveData("")
    fun  getMusics(option: String, country: String?, offset: Int = 0){
        _musics.postValue(listOf())
        _status.postValue(ApiStatus.LOADING)
        viewModelScope.launch {
            try{
                if(country == null){
                    Services.retrofitService.getMusics(option, offset).let {
                        _musics.value = it.data
                    }
                    _status.value = ApiStatus.DONE
                }
                else{
                    Services.retrofitService.getMusics(option, offset,country).let {
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