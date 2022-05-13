package com.example.musicdownloader.manager

import android.media.AudioAttributes
import android.media.MediaPlayer
import java.io.IOException

object MediaManager {
    private var mediaPlayer: MediaPlayer? = null
    private var isPause = true
    fun createMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
    }

    fun getMediaPlayer() : MediaPlayer?{
        return mediaPlayer
    }

    fun isPause(): Boolean{
        return isPause
    }

    fun playMusic( url: String) {
        isPause = false
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        try {
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.setOnPreparedListener { obj: MediaPlayer -> obj.start() }
            mediaPlayer?.prepareAsync()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun resetMedia() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    fun pauseMedia() {
        if (mediaPlayer != null) {
            isPause = true
            mediaPlayer!!.pause()
        }
    }

    fun resumeMedia() {
        if (mediaPlayer != null) {
            isPause = false
            mediaPlayer!!.start()
        }
    }
}