package com.example.musicdownloader.manager

import android.util.Log
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.MusicDownloaded

enum class RepeatStatus {RepeatOneMusic, NoRepeat, RepeatListMusic }

object MusicManager {
    private var musicList: List<Music> = ArrayList()
    private var currentMusic: Music? = null
    private var repeatStatus = RepeatStatus.NoRepeat
    private var isRandom = false

    fun setRandom(random: Boolean){
        this.isRandom = random
    }
    fun isRandom() = isRandom

    fun setListMusic(musics: List<Music>){
        musicList = musics
    }

    fun setCurrentMusic(music: Music?){
        currentMusic = music
    }

    fun getCurrentMusic(): Music?{
        return currentMusic
    }


    fun getIndexOfCurrentMusic(): Int {
        return musicList.indexOf(currentMusic)
    }

    fun getSizeMusicList(): Int{
        return musicList.size
    }


    fun nextMusic() {
        val pos = getIndexOfCurrentMusic()
        currentMusic = if (pos == musicList.size - 1) {
            musicList[0]
        } else {
            musicList[pos + 1]
        }
    }

    fun previousMusic() {
        val pos = getIndexOfCurrentMusic()
        currentMusic = if (pos == 0) {
            musicList[musicList.size - 1]
        } else {
            musicList[pos - 1]
        }
    }

    fun setRepeatStatus(repeatStatus: RepeatStatus){
        this.repeatStatus = repeatStatus
    }
    fun getRepeatStatus(): RepeatStatus = repeatStatus

    fun randomMusic(){
        val pos = (musicList.indices).random()
        if(pos != getIndexOfCurrentMusic()){
            currentMusic = musicList[pos]
        }else{
            randomMusic()
        }
    }
}