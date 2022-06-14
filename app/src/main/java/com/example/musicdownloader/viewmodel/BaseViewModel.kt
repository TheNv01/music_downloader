package com.example.musicdownloader.viewmodel

import android.app.Application
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicdownloader.R
import com.example.musicdownloader.database.MusicRoomDatabase
import com.example.musicdownloader.manager.DownloadingManager
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.MusicDownloading
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.repository.FavoriteRepository
import com.example.musicdownloader.repository.PlaylistRepository
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {

    lateinit var options: ArrayList<Option>

    protected val playlistRepository: PlaylistRepository
    protected val favoriteRepository: FavoriteRepository

    init {
        initOption()
        val playlistDAO = MusicRoomDatabase
            .MusicDatabaseBuilder
            .getInstance(application.applicationContext)
            .playlistDAO()
        playlistRepository = PlaylistRepository(playlistDAO)

        val favoriteDAO = MusicRoomDatabase
            .MusicDatabaseBuilder
            .getInstance(application.applicationContext)
            .favoriteDAO()
        favoriteRepository = FavoriteRepository(favoriteDAO)
    }

    private fun initOption(){
        options = ArrayList()
        options.add(Option("Add to playlist", R.drawable.ic_add_to_playlist))
        options.add(Option("Download Song", R.drawable.ic_white_dowload))
        options.add(Option("Add to Favorite", R.drawable.ic_favorite))
        options.add(Option("Share", R.drawable.ic_share))
    }

    fun insertMusicToFavorite(music: Music){
        viewModelScope.launch(Dispatchers.IO) {
            favoriteRepository.insertMusicToFavorite(music)
        }
    }

    fun existInFavorite(id: String): Music?{
        return favoriteRepository.getListMusic(id)
    }

    fun startDownload(music: Music,file: File){

        val path = if(music.name!!.contains("/")){
            file.toString() +"/" + music.name.split("")[0].plus(".mp3")
        } else{
            file.toString() +"/"+ music.name.plus(".mp3")
        }

        if(File(path).exists()){
            Toast.makeText(getApplication(), "Can't download because it was downloaded or downloading", Toast.LENGTH_SHORT).show()
        }
        else{
            val request = Request(music.audioDownload!!, path)
            request.priority = Priority.HIGH
            request.networkType = NetworkType.ALL

            val musicDownloading = MusicDownloading(
                music.name!!,
                music.artistName!!,
                music.image!!,
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
                            MusicDonwnloadedManager.getMusicFromExternal()
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
    }
}