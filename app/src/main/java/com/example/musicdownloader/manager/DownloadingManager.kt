package com.example.musicdownloader.manager

import android.content.Context
import coil.fetch.Fetcher
import com.example.musicdownloader.model.MusicDownloading
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration

object DownloadingManager {

    private val musicDownloadings =  ArrayList<MusicDownloading>()

    var fetch: Fetch? = null

    fun listDownloading(): ArrayList<MusicDownloading> = musicDownloadings

    fun getFetch(context: Context): Fetch{
        if(fetch == null){
            val fetchConfiguration: FetchConfiguration = FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(3)
                .build()

            fetch = Fetch.getInstance(fetchConfiguration)
        }
        return fetch!!
    }
}