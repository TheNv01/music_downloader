package com.example.musicdownloader.manager

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import java.io.IOException


object MediaManager {
    private var mediaPlayer: MediaPlayer? = null
    private var isPause  = true

    fun isPause(): Boolean{
        return isPause
    }

    fun playMusic(url: String, completionListener: MediaPlayer.OnCompletionListener ) {
        isPause = false
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        try {
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.setOnPreparedListener { obj: MediaPlayer -> obj.start() }
            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnCompletionListener(completionListener)

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

            Log.d("asfasf", "paiseeeeeee")
        }
    }

    fun resumeMedia() {
        if (mediaPlayer != null) {
            isPause = false
            mediaPlayer!!.start()
        }
    }

    fun getProgress(): Int {
        return if (mediaPlayer != null) {
            mediaPlayer!!.currentPosition
        } else 0
    }

    fun setProgress(milli: Int) {
        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo(milli)
        }
    }
}