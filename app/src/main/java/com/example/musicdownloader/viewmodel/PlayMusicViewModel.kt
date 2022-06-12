package com.example.musicdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.R
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.General
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.networking.Services
import com.tonyodev.fetch2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PlayMusicViewModel(application: Application) : BaseViewModel(application) {

    lateinit var optionsDownloaded: ArrayList<Option>

    private var _linkAudio = MutableLiveData<String>()
    val linkAudio: LiveData<String> = _linkAudio

    private var _isExisted = MutableLiveData<Boolean>()
    val isExisted: LiveData<Boolean> = _isExisted

    var isPrepared = MutableLiveData(false)
    init {

        MusicManager.getCurrentMusic()?.let {
            getLinkAudio(it)
        }
    }


    fun deleteMusicFromFavorite(id: String){
        viewModelScope.launch {
            favoriteRepository.deletePlaylist(id)
        }
    }

    fun existInFavorite(){
        MusicManager.getCurrentMusic()?.let {
            viewModelScope.launch(Dispatchers.IO) {
                if(favoriteRepository.getListMusic(it.id) == null){
                    _isExisted.postValue(false)
                }
                else{
                    _isExisted.postValue(true)
                }
            }
        }
    }

    fun initOption(haveDownloadOption: Boolean){

        optionsDownloaded = ArrayList()

        optionsDownloaded.add(Option("Add to Playlist", R.drawable.ic_add_to_playlist))
        optionsDownloaded.add(Option("Add to Favorite", R.drawable.ic_favorite))

        if(haveDownloadOption){
            optionsDownloaded.add(Option("Download Song", R.drawable.ic_white_dowload))
        }
    }

    private fun getLinkAudio(music: Music) {
            if(music.source.equals("SC")){
                Services.retrofitService.getLinkSourceSc(music.id).enqueue(object : Callback<General<String>> {
                    override fun onResponse(call: Call<General<String>>, response: Response<General<String>>) {
                        if (response.isSuccessful) {
                            _linkAudio.postValue(response.body()?.data)
                        }
                    }
                    override fun onFailure(call: Call<General<String>>, t: Throwable) {}
                })
            } else{
                _linkAudio.postValue(music.audio!!)
            }

    }
}