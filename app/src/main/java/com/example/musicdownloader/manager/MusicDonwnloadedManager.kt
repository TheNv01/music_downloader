package com.example.musicdownloader.manager

import com.example.musicdownloader.model.MusicDownloaded

object MusicDonwnloadedManager {

    var musicsDownloaded: List<MusicDownloaded> = ArrayList()
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