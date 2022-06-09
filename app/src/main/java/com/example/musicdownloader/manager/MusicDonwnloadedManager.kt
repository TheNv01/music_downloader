package com.example.musicdownloader.manager

import android.graphics.BitmapFactory
import com.example.musicdownloader.Utils
import com.example.musicdownloader.model.MusicDownloaded
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File


object MusicDonwnloadedManager {

    var musicsDownloaded: ArrayList<MusicDownloaded> = ArrayList()
    var currentMusicDownloaded: MusicDownloaded? = null


    fun getMusicFromExternal(){
        val file = File(Utils.PATH)
        if (file.listFiles()?.isNotEmpty() == true) {
            musicsDownloaded.clear()
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
                        val duration = mediaMetadataRetriever.extractMetadata(
                            FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
                        var songImage = art?.let {
                            BitmapFactory
                                .decodeByteArray(art, 0, it.size)
                        }
                        if(duration.length > 1){
                            musicsDownloaded.add(MusicDownloaded(
                                songName, artist, musicFile.path, songImage, duration.substring(0, 3)
                            ))
                        }
                        else{
                            musicsDownloaded.add(MusicDownloaded(
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

    fun getIndexOfCurrentMusic(): Int {
        return musicsDownloaded.indexOf(currentMusicDownloaded)
    }

    fun getSizeMusicList(): Int{
        return musicsDownloaded.size
    }


    fun nextMusic() {
        val pos = getIndexOfCurrentMusic()
        currentMusicDownloaded = if (pos == musicsDownloaded.size - 1) {
            musicsDownloaded[0]
        } else {
            musicsDownloaded[pos + 1]
        }
    }

    fun previousMusic() {
        val pos = getIndexOfCurrentMusic()
        currentMusicDownloaded = if (pos == 0) {
            musicsDownloaded[musicsDownloaded.size - 1]
        } else {
            musicsDownloaded[pos - 1]
        }
    }

    fun randomMusic(){
        val pos = (musicsDownloaded.indices).random()
        if(pos != getIndexOfCurrentMusic()){
            currentMusicDownloaded = musicsDownloaded[pos]
        }else{
            randomMusic()
        }
    }


}