package com.example.musicdownloader.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.R
import com.example.musicdownloader.model.General
import com.example.musicdownloader.model.Item
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.networking.Api
import com.example.musicdownloader.networking.Services
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import kotlin.Exception

enum class ApiStatus { LOADING, ERROR, DONE }

class HomeViewModel: BaseViewModel() {

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    private var _trends = MutableLiveData<List<Music>>()
    val trends: LiveData<List<Music>> = _trends

    private var _topRatings = MutableLiveData<List<Item>>()
    val topRatings: LiveData<List<Item>> = _topRatings

    private var _topDownloads = MutableLiveData<List<Item>>()
    val topDownloads: LiveData<List<Item>> = _topDownloads

    private var _topListeneds = MutableLiveData<List<Item>>()
    val topListeneds: LiveData<List<Item>> = _topListeneds

    private var _genres = MutableLiveData<List<Item>>()
    val genres: LiveData<List<Item>> = _genres

    init {
        insertTrendingData()
        insertTopRatingData()
        insertGenresData()
        insertTopDownloadData()
        insertTopListenedData()
    }

    private fun insertTrendingData(){
        _status.value = ApiStatus.LOADING
        viewModelScope.launch {
            try{
                Services.retrofitService.getPopulars().let {
                    _trends.value = it.data
                    _status.value = ApiStatus.DONE
                }
            } catch (e: Exception){
                _status.value = ApiStatus.ERROR
                _trends.value = listOf()
            }
        }
    }

    private fun insertTopRatingData(){
        val data = ArrayList<Item>()

        for (i in 1..5) {
            data.add(Item("Last Christmas", "Devon Lane", R.drawable.ic_background_rating))
        }
        _topRatings.value = data
    }

    private fun insertTopDownloadData(){
        val data = ArrayList<Item>()

        for (i in 1..7) {
            data.add(Item("Last Christmas", "Devon Lane", R.drawable.test_download))
        }
        _topDownloads.value = data
    }

    private fun insertTopListenedData(){
        val data = ArrayList<Item>()

        for (i in 1..5) {
            data.add(Item("Last Christmas", "Devon Lane", R.drawable.test_top_listened))
        }
        _topListeneds.value = data
    }

    private fun insertGenresData(){
        val data = ArrayList<Item>()

        for (i in 1..5) {
            data.add(Item("Pop", "", R.drawable.test_genres))
        }
        _genres.value = data
    }
}