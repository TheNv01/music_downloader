package com.example.musicdownloader.viewmodel

import android.app.Application
import android.graphics.BitmapFactory
import android.os.Environment
import com.example.musicdownloader.R
import com.example.musicdownloader.model.MusicDownloaded
import com.example.musicdownloader.model.Option
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File

class DownloadViewModel(app: Application) : BaseViewModel(app) {
    val optionsDownloading = ArrayList<Option>()
    val optionsDownloaded = ArrayList<Option>()


    init {
        initOptionDownloading()
        initOptionDownloaded()
    }

    private fun initOptionDownloading(){

        optionsDownloading.add(Option("Remove Download", R.drawable.ic_remove))
        optionsDownloading.add(Option("Pause Download", R.drawable.ic_pause_download))
        optionsDownloading.add(Option("Share", R.drawable.ic_share))

    }

    private fun initOptionDownloaded(){
        optionsDownloaded.add(Option("Add to playlist", R.drawable.ic_add_to_playlist))
        optionsDownloaded.add(Option("Remove Downloaded Song", R.drawable.ic_delete))
        optionsDownloaded.add(Option("Set as Ringtone", R.drawable.ic_bell))
        optionsDownloaded.add(Option("Share", R.drawable.ic_share))
    }

}