package com.example.musicdownloader.manager

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.provider.MediaStore
import com.example.musicdownloader.model.MusicDownloaded
import java.io.File


object MusicDonwnloadedManager {

    var musicsDownloaded: ArrayList<MusicDownloaded> = ArrayList()
    var currentMusicDownloaded: MusicDownloaded? = null

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