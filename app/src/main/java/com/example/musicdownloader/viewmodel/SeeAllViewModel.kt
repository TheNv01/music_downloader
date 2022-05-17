package com.example.musicdownloader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.networking.Services
import kotlinx.coroutines.launch

class SeeAllViewModel: BaseViewModel() {
    private var _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    private var _allGenre = MutableLiveData<List<Genres>>()
    val allGenre: LiveData<List<Genres>> = _allGenre

    private var _musics = MutableLiveData<List<Music>>()
    val musics: LiveData<List<Music>> = _musics


    init {
        getAllGenre()
    }
    private fun getAllGenre(){
        _status.value = ApiStatus.LOADING
        viewModelScope.launch {
            try{
                Services.retrofitService.getGenres().let {
                    val listTemp = it.data as MutableList<Genres>
                    listTemp.add(0, Genres("",0, "All", "", ""))
                    _allGenre.value = listTemp
                    _status.value = ApiStatus.DONE
                }
            } catch (e: Exception){
                _allGenre.value = listOf()
            }
        }

    }
    fun  getMusics(option: String, country: String?, offset: Int = 0,){
        viewModelScope.launch {
            try{
                if(country == null){
                    Services.retrofitService.getMusics(option, offset).let {
                        _musics.value = it.data
                    }
                }
                else{
                    Services.retrofitService.getMusics(option, offset,country).let {
                        _musics.value = it.data
                    }
                }

            } catch (e: Exception){
                _musics.value = listOf()
            }
        }
    }
}