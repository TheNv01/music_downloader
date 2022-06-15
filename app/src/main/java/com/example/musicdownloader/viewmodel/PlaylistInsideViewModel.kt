package com.example.musicdownloader.viewmodel

import android.app.Application
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.R
import com.example.musicdownloader.Utils
import com.example.musicdownloader.manager.DownloadingManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.model.Playlist
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistInsideViewModel(application: Application) : BaseViewModel(application) {

    private var _musics = MutableLiveData<List<Music>>()
    val musics: LiveData<List<Music>> = _musics

    lateinit var request: Request

    private var _isDownloadComplete = MutableLiveData<Boolean>()
    val isDownloadComplete: LiveData<Boolean> = _isDownloadComplete

    val existingPlaylist: LiveData<List<Playlist>> = playlistRepository.playlists

    val optionsPlaylist = ArrayList<Option>()
    val optionsSong = ArrayList<Option>()

    init {
        initOption()
    }

    private fun initOption(){
        optionsPlaylist.add(Option("Rename playlist", R.drawable.ic_rename))
        optionsPlaylist.add(Option("Delete playlist", R.drawable.ic_delete))

        optionsSong.add(Option("Remove song from playlist", R.drawable.ic_delete))
        optionsSong.add(Option("Share", R.drawable.ic_share))
        optionsSong.add(Option("Set as ringtone", R.drawable.ic_bell))
        optionsSong.add(Option("Add to Favorite", R.drawable.ic_favorite))

    }

    fun startDownloadss(music: Music, path: String){

        request = Request(music.audioDownload!!, path)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL

        viewModelScope.launch(Dispatchers.IO) {
            DownloadingManager.getFetch(getApplication()).enqueue(request, {}, {})
            DownloadingManager.fetch!!.addListener(
                object : FetchListener {
                    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {}
                    override fun onRemoved(download: Download) {}
                    override fun onCompleted(download: Download) {
                       _isDownloadComplete.postValue(true)
                    }
                    override fun onDeleted(download: Download) {}
                    override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {}
                    override fun onError(download: Download, error: Error, throwable: Throwable?) {
                        DownloadingManager.fetch!!.retry(request.id)
                    }
                    override fun onPaused(download: Download) {}
                    override fun onResumed(download: Download) {}
                    override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {
                        _isDownloadComplete.postValue(false)
                    }
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


    fun getMusics(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            _musics.postValue(playlistRepository.getListMusic(id).musics)
        }
    }

    fun deletePlaylist(id: Int){
        playlistRepository.deletePlaylist(id)
    }

    fun removeMusic(name: String,id: Int, music: Music){
        viewModelScope.launch(Dispatchers.IO) {
            val musics = playlistRepository.getListMusic(id).musics
            musics.remove(music)
            playlistRepository.updatePlaylist(Playlist(name, musics, id))
        }
    }

    fun renamePlaylist(name: String, id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.renamePlaylist(name, id)
        }
    }
}