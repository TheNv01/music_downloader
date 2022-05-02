package com.example.musicdownloader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicdownloader.R
import com.example.musicdownloader.model.Item

class HomeViewModel: BaseViewModel() {

    private var _trends = MutableLiveData<List<Item>>()
    val trends: LiveData<List<Item>> = _trends

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
        val data = ArrayList<Item>()

        for (i in 1..5) {
            data.add(Item("Last Christmas", "Devon Lane", R.drawable.trending_test))
        }
        _trends.value = data
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

        for (i in 1..5) {
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