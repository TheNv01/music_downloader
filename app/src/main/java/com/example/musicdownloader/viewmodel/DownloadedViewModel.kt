package com.example.musicdownloader.viewmodel


import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import com.example.musicdownloader.R
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.MusicDownloaded
import com.example.musicdownloader.model.Option
import java.io.File


class DownloadedViewModel : BaseViewModel() {
    val optionsDownloaded = ArrayList<Option>()
    val downloadeds = ArrayList<MusicDownloaded>()

    init {
        initOption()
    }

    private fun initOption(){
        optionsDownloaded.add(Option("Add to playlist", R.drawable.ic_add_to_playlist))
        optionsDownloaded.add(Option("Remove Downloaded Song", R.drawable.ic_delete))
        optionsDownloaded.add(Option("Set as Ringtone", R.drawable.ic_bell))
    }

    fun getListMusicDownloaded() {
        val file = File(Environment.getExternalStorageDirectory().toString().plus("/music downloader"))
        if (file.listFiles()?.isNotEmpty() == true) {
            downloadeds.clear()
            val mediaMetadataRetriever = MediaMetadataRetriever()
            file.listFiles()?.forEach { musicFile ->
                mediaMetadataRetriever.setDataSource(musicFile.path)
                val songName =
                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                val artist =
                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                val art = mediaMetadataRetriever.embeddedPicture
                val duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.substring(0,3)
                val songImage = art?.let {
                    BitmapFactory
                        .decodeByteArray(art, 0, it.size)
                }
                downloadeds.add(MusicDownloaded(
                    songName, artist, musicFile.path, songImage, duration
                ))
            }

        }
    }

}