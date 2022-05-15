package com.example.musicdownloader.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.networking.Services
import kotlinx.coroutines.launch
import kotlin.Exception

enum class ApiStatus { LOADING, DONE }

class HomeViewModel: BaseViewModel() {

    private var _statusTrending = MutableLiveData<ApiStatus>()
    val statusTrending: LiveData<ApiStatus> = _statusTrending
    private var _trends = MutableLiveData<List<Music>>()
    val trends: LiveData<List<Music>> = _trends

    private var _statusRating = MutableLiveData<ApiStatus>()
    val statusRating: LiveData<ApiStatus> = _statusRating
    private var _topRatings = MutableLiveData<List<Music>>()
    val topRatings: LiveData<List<Music>> = _topRatings

    private var _statusDownload = MutableLiveData<ApiStatus>()
    val statusDownload: LiveData<ApiStatus> = _statusDownload
    private var _topDownloads = MutableLiveData<List<Music>>()
    val topDownloads: LiveData<List<Music>> = _topDownloads

    private var _statusListened = MutableLiveData<ApiStatus>()
    val statusListened: LiveData<ApiStatus> = _statusListened
    private var _topListeneds = MutableLiveData<List<Music>>()
    val topListeneds: LiveData<List<Music>> = _topListeneds

    private var _statusGenres = MutableLiveData<ApiStatus>()
    val statusGenres: LiveData<ApiStatus> = _statusGenres
    private var _genres = MutableLiveData<List<Genres>>()
    val genres: LiveData<List<Genres>> = _genres

    init {
        getGenresData()
    }

    fun factoryMusics(option: String, country: String?, offset: Int = 0){
        _statusTrending.value = ApiStatus.LOADING
        _statusRating.value = ApiStatus.LOADING
        _statusDownload.value = ApiStatus.LOADING
        _statusListened.value = ApiStatus.LOADING
        viewModelScope.launch {
            when(option){
                "popular" ->{
                    getMusics(_statusTrending, _trends, option, offset, country)
                }
                "listened" ->{
                    getMusics(_statusListened, _topListeneds, option, offset, country)
                }
                "ranking" ->{
                    getMusics(_statusRating, _topRatings, option, offset, country)
                }
                "download" ->{
                    getMusics(_statusDownload, _topDownloads, option, offset, country)
                }
            }
        }
    }

    private suspend fun getMusics(
        status: MutableLiveData<ApiStatus>,
        musics: MutableLiveData<List<Music>>,
        option: String,
        offset: Int = 0,
        country: String?){
        try{
            if(country == null){
                Services.retrofitService.getMusics(option, offset).let {
                    musics.value = it.data
                    status.value = ApiStatus.DONE
                }
            }
            else{
                Services.retrofitService.getMusics(option, offset,country).let {
                    musics.value = it.data
                    status.value = ApiStatus.DONE
                }
            }

        } catch (e: Exception){
            musics.value = listOf()
        }
    }

    private fun getGenresData(){
        _statusGenres.value = ApiStatus.LOADING
        viewModelScope.launch {
            try{
                Services.retrofitService.getGenres().let {
                    _genres.value = it.data.subList(0, 5)
                    _statusGenres.value = ApiStatus.DONE
                }
            } catch (e: Exception){
                _genres.value = listOf()
            }
        }
    }
}