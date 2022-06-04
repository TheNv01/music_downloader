package com.example.musicdownloader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.R
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.networking.Services
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayMusicViewModel: BaseViewModel() {
    lateinit var optionsDownloaded: ArrayList<Option>

    private var _linkAudio = MutableLiveData<String>()
    val linkAudio: LiveData<String> = _linkAudio

    init {

        MusicManager.getCurrentMusic()?.let {
            getLinkAudio(it)
        }
    }

    fun initOption(haveShareOption: Boolean ?= null){

        optionsDownloaded = ArrayList()

        optionsDownloaded.add(Option("Add to Playlist", R.drawable.ic_add_to_playlist))
        optionsDownloaded.add(Option("Add to Favorite", R.drawable.ic_favorite))

        if(haveShareOption != null){
            if(haveShareOption == true){
                optionsDownloaded.add(Option("Download Song", R.drawable.ic_white_dowload))
                optionsDownloaded.add(Option("Share", R.drawable.ic_share))
            }
            else{
                optionsDownloaded.add(Option("Download Song", R.drawable.ic_white_dowload))
            }
        }
    }

    private fun getLinkAudio(music: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            if(music.source.equals("SC")){
                _linkAudio.postValue(Services.retrofitService.getLinkSourceSc(music.id).data)
            } else{
                _linkAudio.postValue(music.audio!!)
            }

        }
    }
}