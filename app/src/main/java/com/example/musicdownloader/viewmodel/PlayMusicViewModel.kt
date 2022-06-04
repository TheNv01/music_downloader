package com.example.musicdownloader.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.R
import com.example.musicdownloader.database.MusicRoomDatabase
import com.example.musicdownloader.manager.DownloadingManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.MusicDownloading
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.networking.Services
import com.example.musicdownloader.repository.FavoriteRepository
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PlayMusicViewModel(application: Application): AndroidViewModel(application) {

    lateinit var optionsDownloaded: ArrayList<Option>

    private var _linkAudio = MutableLiveData<String>()
    val linkAudio: LiveData<String> = _linkAudio

    private var _isExisted = MutableLiveData<Boolean>()
    val isExisted: LiveData<Boolean> = _isExisted

    private val repository: FavoriteRepository

    init {
        val favoriteDAO = MusicRoomDatabase
            .MusicDatabaseBuilder
            .getInstance(application.applicationContext).favoriteDAO()
        repository = FavoriteRepository(favoriteDAO)
        MusicManager.getCurrentMusic()?.let {
            getLinkAudio(it)
        }
    }

    fun insertMusicToFavorite(music: Music){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMusicToFavorite(music)
        }
    }

    fun deleteMusicFromFavorite(id: String){
        viewModelScope.launch {
            repository.deletePlaylist(id)
        }
    }

    fun existInFavorite(){
        MusicManager.getCurrentMusic()?.let {
            viewModelScope.launch(Dispatchers.IO) {
                if(repository.getListMusic(it.id) == null){
                    _isExisted.postValue(false)
                }
                else{
                    _isExisted.postValue(true)
                }
            }
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

    fun startDownload(file: File){
        val path = file.toString() +"/"+ MusicManager.getCurrentMusic()!!.name.plus(".mp3")
        val request = Request(MusicManager.getCurrentMusic()!!.audioDownload!!, path)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL

        val musicDownloading = MusicDownloading(MusicManager.getCurrentMusic()!!.name!!,
            MusicManager.getCurrentMusic()!!.artistName!!,
            request)

        DownloadingManager.listDownloading().add(musicDownloading)

        viewModelScope.launch(Dispatchers.IO) {
            DownloadingManager.getFetch(getApplication()).enqueue(request,
                { Toast.makeText(getApplication(), "Downloading...", Toast.LENGTH_SHORT).show()},
                { Toast.makeText(getApplication(), "Something wrong!...", Toast.LENGTH_SHORT).show() })
            DownloadingManager.fetch!!.addListener(
                object : FetchListener {
                    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {}
                    override fun onRemoved(download: Download) {}
                    override fun onCompleted(download: Download) {
                        DownloadingManager.listDownloading().remove(musicDownloading)
                    }
                    override fun onDeleted(download: Download) {}
                    override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {}
                    override fun onError(download: Download, error: Error, throwable: Throwable?) {
                        DownloadingManager.fetch!!.retry(request.id)
                    }
                    override fun onPaused(download: Download) {}
                    override fun onResumed(download: Download) {}
                    override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {}
                    override fun onWaitingNetwork(download: Download) {}
                    override fun onAdded(download: Download) {}
                    override fun onCancelled(download: Download) {}
                    override fun onProgress(
                        download: Download,
                        etaInMilliSeconds: Long,
                        downloadedBytesPerSecond: Long
                    ) {}

                })
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