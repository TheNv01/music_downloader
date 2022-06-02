package com.example.musicdownloader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.networking.Services
import kotlinx.coroutines.launch

class SeeAllGenresViewModel: BaseViewModel() {

    private var _statusGenres = MutableLiveData<ApiStatus>()
    val statusGenres: LiveData<ApiStatus> = _statusGenres
    private var _genres = MutableLiveData<List<Genres>>()
    val genres: LiveData<List<Genres>> = _genres

    init {
        getGenresData()
    }

    private fun getGenresData(){
        _statusGenres.value = ApiStatus.LOADING
        viewModelScope.launch {
            try{
                Services.retrofitService.getGenres().let {
                    _genres.value = it.data
                    _statusGenres.value = ApiStatus.DONE
                }
            } catch (e: Exception){
                _genres.value = listOf()
            }
        }
    }
}