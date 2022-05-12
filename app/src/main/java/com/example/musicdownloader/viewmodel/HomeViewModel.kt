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

    private var _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    private var _trends = MutableLiveData<List<Music>>()
    val trends: LiveData<List<Music>> = _trends

    private var _topRatings = MutableLiveData<List<Music>>()
    val topRatings: LiveData<List<Music>> = _topRatings

    private var _topDownloads = MutableLiveData<List<Music>>()
    val topDownloads: LiveData<List<Music>> = _topDownloads

    private var _topListeneds = MutableLiveData<List<Music>>()
    val topListeneds: LiveData<List<Music>> = _topListeneds

    private var _genres = MutableLiveData<List<Genres>>()
    val genres: LiveData<List<Genres>> = _genres

    init {
        getTrendingData()
        getTopRatingData()
        getGenresData()
        getTopDownloadData()
        getTopListenedData()
    }

    private fun getTrendingData(){
        _status.value = ApiStatus.LOADING
        viewModelScope.launch {
            try{
                Services.retrofitService.getPopulars().let {
                    _trends.value = it.data
                    _status.value = ApiStatus.DONE
                }
            } catch (e: Exception){
                _trends.value = listOf()
            }
        }
    }

    private fun getTopRatingData(){
        viewModelScope.launch {
            try{
                Services.retrofitService.getTopRating().let {
                    _topRatings.value = it.data.subList(0, 5)
                }
            } catch (e: Exception){
                _topRatings.value = listOf()
            }
        }
    }

    private fun getTopDownloadData(){
        viewModelScope.launch {
            try{
                Services.retrofitService.getTopDownload().let {
                    _topDownloads.value = it.data.subList(0, 5)
                }
            } catch (e: Exception){
                _topDownloads.value = listOf()
            }
        }
    }

    private fun getTopListenedData(){
        viewModelScope.launch {
            try{
                Services.retrofitService.getTopListened().let {
                    _topListeneds.value = it.data.subList(0, 5)
                }
            } catch (e: Exception){
                _topListeneds.value = listOf()
            }
        }
    }

    private fun getGenresData(){
        viewModelScope.launch {
            try{
                Services.retrofitService.getGenres().let {
                    _genres.value = it.data.subList(0, 5)
                    Log.d("tag", it.data[0].image.toString())
                }
            } catch (e: Exception){
                _genres.value = listOf()
            }
        }
    }
}