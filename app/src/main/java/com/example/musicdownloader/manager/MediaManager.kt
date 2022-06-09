package com.example.musicdownloader.manager

import android.media.AudioAttributes
import android.media.MediaPlayer
import java.io.IOException

object MediaManager {
    var mediaPlayer: MediaPlayer? = MediaPlayer()
    var isPause  = false


    fun playMusic(url: String, completionListener: MediaPlayer.OnCompletionListener ) {
        isPause = false
        mediaPlayer?.reset()
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        try {
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()

            mediaPlayer?.setOnCompletionListener(completionListener)

        }catch (e: IllegalArgumentException){
            e.printStackTrace()
        } catch (e: IllegalStateException){
            e.printStackTrace()
        }catch (e: IOException) {
            e.printStackTrace()
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