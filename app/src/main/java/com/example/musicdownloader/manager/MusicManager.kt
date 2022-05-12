package com.example.musicdownloader.manager

import com.example.musicdownloader.model.Music

object MusicManager {
    private var musicList: MutableList<Music> = ArrayList()
    private var currentMusic: Music? = null

    fun setListMusic(musics: ArrayList<Music>){
        musicList = musics
    }

    fun setCurrentMusic(music: Music){
        currentMusic = music
    }

    fun getCurrentMusic(): Music{
        return currentMusic!!
    }


    private fun getIndexOfSong(music: Music?): Int {
        return musicList.indexOf(music)
    }

    fun nextSong() {
        val pos = getIndexOfSong(currentMusic)
        currentMusic = if (pos == musicList.size - 1) {
            musicList[0]
        } else {
            musicList[pos + 1]
        }
    }

    fun previousSong() {
        val pos = getIndexOfSong(currentMusic)
        currentMusic = if (pos == 0) {
            musicList[musicList.size - 1]
        } else {
            musicList[pos - 1]
        }
    }
}