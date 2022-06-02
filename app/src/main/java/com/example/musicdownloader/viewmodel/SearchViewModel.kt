package com.example.musicdownloader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.networking.Services
import kotlinx.coroutines.launch

class SearchViewModel: BaseViewModel() {

    private var _musics = MutableLiveData<List<Music>>()
    val musics: LiveData<List<Music>> = _musics

    init {
        getMusics(null)
    }

    fun  getMusics(keyword: String?, offset: Int = 0){
        viewModelScope.launch {
            try{
                if(keyword == null){
                    Services.retrofitService.searchMusic( offset).let {
                        _musics.value = it.data
                    }
                }
                else{
                    Services.retrofitService.searchMusic(offset, keyword).let {
                        _musics.value = it.data
                    }
                }

            } catch (e: Exception){
                _musics.value = listOf()
            }
        }
    }
}