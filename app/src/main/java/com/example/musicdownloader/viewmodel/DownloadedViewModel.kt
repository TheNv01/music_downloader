package com.example.musicdownloader.viewmodel


import android.app.Application
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicdownloader.R
import com.example.musicdownloader.model.MusicDownloaded
import com.example.musicdownloader.model.Option
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File

class DownloadedViewModel(val applicat: Application) : BaseViewModel(applicat) {
    val optionsDownloaded = ArrayList<Option>()
    val downloadeds =  ArrayList<MusicDownloaded>()



    init {
        initOption()
    }

    private fun initOption(){
        optionsDownloaded.add(Option("Add to playlist", R.drawable.ic_add_to_playlist))
        optionsDownloaded.add(Option("Remove Downloaded Song", R.drawable.ic_delete))
        optionsDownloaded.add(Option("Set as Ringtone", R.drawable.ic_bell))
        optionsDownloaded.add(Option("Share", R.drawable.ic_share))
    }


    fun getMusicFromExternal(){
        val file = File(Environment.getExternalStorageDirectory().toString().plus("/music downloader"))
        if (file.listFiles()?.isNotEmpty() == true) {
            downloadeds.clear()
            val mediaMetadataRetriever = FFmpegMediaMetadataRetriever()
            file.listFiles()?.forEach { musicFile ->
                if(musicFile.length() != 0L){
                    try {
                        mediaMetadataRetriever.setDataSource(musicFile.path)
                        val songName =
                            mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE)
                        val artist =
                            mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST)
                        val art = mediaMetadataRetriever.embeddedPicture
                        val duration = mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
                        var songImage = art?.let {
                            BitmapFactory
                                .decodeByteArray(art, 0, it.size)
                        }
                        if(songImage == null){
                            songImage = BitmapFactory.decodeResource(applicat.resources, R.drawable.bg_playlist)
                        }
                        if(duration.length > 1){
                            downloadeds.add(MusicDownloaded(
                                songName, artist, musicFile.path, songImage, duration.substring(0, 3)
                            ))
                        }
                        else{
                            downloadeds.add(MusicDownloaded(
                                songName, artist, musicFile.path, songImage, duration
                            ))
                        }

                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}