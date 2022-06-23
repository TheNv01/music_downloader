package com.example.musicdownloader

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.musicdownloader.manager.*
import com.example.musicdownloader.model.General
import com.example.musicdownloader.model.MessageEvent
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.MusicDownloaded
import com.example.musicdownloader.networking.Services
import com.example.musicdownloader.view.MainActivity
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusicService : Service() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)



    companion object {
        const val ACTION_START = 1
        const val ACTION_PAUSE = 2
        const val ACTION_RESUME = 3
        const val ACTION_NEXT = 4
        const val ACTION_PREVIOUS = 5
        const val ACTION_CLOSE = 6
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val action: Int = intent.getIntExtra("action", 0)
        if (action != 0) {
            when (action) {
                ACTION_START ->{
                    if(MusicDonwnloadedManager.currentMusicDownloaded == null) {

                        MusicManager.getCurrentMusic()?.let { getLinkAudio(it) }
                    }
                    else {
                        MusicDonwnloadedManager.currentMusicDownloaded?.let { startMusicDownloaded(it) }
                    }

                }
                ACTION_RESUME -> {
                    MediaManager.resumeMedia()
                }
                ACTION_PAUSE -> {
                    MediaManager.pauseMedia()
                }
                ACTION_PREVIOUS ->{
                    if(MusicDonwnloadedManager.currentMusicDownloaded == null) {
                        MusicManager.previousMusic()
                    }
                    else {
                        MusicDonwnloadedManager.previousMusic()
                    }

                }
                ACTION_NEXT ->{
                    if(MusicManager.isRandom()){
                        if(MusicDonwnloadedManager.currentMusicDownloaded == null) {
                            MusicManager.randomMusic()
                        }
                        else {
                            MusicDonwnloadedManager.randomMusic()
                        }
                    }
                    else{
                        if(MusicDonwnloadedManager.currentMusicDownloaded == null) {
                            MusicManager.nextMusic()
                        }
                        else {
                            MusicDonwnloadedManager.nextMusic()
                        }
                    }
                }
                ACTION_CLOSE -> {
                    stopSelf()
                }
            }
            EventBus.getDefault().post(MessageEvent(action))
            if(MusicDonwnloadedManager.currentMusicDownloaded == null) {
                MusicManager.getCurrentMusic()?.let { pushNotification(it) }
            }
            else {
                pushNotification()
            }
        }
        return START_NOT_STICKY
    }

    private fun startMusicDownloaded(musicDownloaded: MusicDownloaded){
        //MediaManager.resetMedia()
            MediaManager.playMusic(musicDownloaded.uri.toString()){
                when(MusicManager.getRepeatStatus()){
                    RepeatStatus.NoRepeat ->{
                        if(MusicDonwnloadedManager.getIndexOfCurrentMusic() == MusicDonwnloadedManager.getSizeMusicList() -1){
                            EventBus.getDefault().post(MessageEvent(ACTION_PAUSE))
                            MediaManager.pauseMedia()
                        }
                        else{
                            MusicDonwnloadedManager.nextMusic()
                            EventBus.getDefault().post(MessageEvent(ACTION_NEXT))
                        }
                    }
                    RepeatStatus.RepeatListMusic ->{
                        MusicDonwnloadedManager.nextMusic()
                        EventBus.getDefault().post(MessageEvent(ACTION_NEXT))
                    }
                    RepeatStatus.RepeatOneMusic ->{
                        EventBus.getDefault().post(MessageEvent(ACTION_NEXT))
                    }
                }
                pushNotification()
            }
        }


    private fun startMusic(url: String) {
        MediaManager.playMusic(url){
            when(MusicManager.getRepeatStatus()){
                RepeatStatus.NoRepeat ->{
                    if(MusicManager.getIndexOfCurrentMusic() == MusicManager.getSizeMusicList() -1){
                        EventBus.getDefault().post(MessageEvent(ACTION_PAUSE))
                        MediaManager.pauseMedia()
                    } else{
                        MusicManager.nextMusic()
                        EventBus.getDefault().post(MessageEvent(ACTION_NEXT))
                    }
                }
                RepeatStatus.RepeatListMusic ->{
                    MusicManager.nextMusic()
                    EventBus.getDefault().post(MessageEvent(ACTION_NEXT))
                }
                RepeatStatus.RepeatOneMusic ->{
                    EventBus.getDefault().post(MessageEvent(ACTION_NEXT))
                }
            }
            MusicManager.getCurrentMusic()?.let { it1 -> pushNotification(it1) }
        }
    }

    private fun getLinkAudio(music: Music){
        if(music.source.equals("SC")){
            Services.retrofitService.getLinkSourceSc(music.id).enqueue(object : Callback<General<String>> {
                override fun onResponse(call: Call<General<String>>, response: Response<General<String>>) {
                    if (response.isSuccessful) {
                        response.body()?.data?.let { startMusic(it) }
                    }
                }
                override fun onFailure(call: Call<General<String>>, t: Throwable) {}
            })
        } else{
            startMusic(music.audio!!)
        }
    }

    private fun pushNotification(music: Music ?= null) {
        val notifyIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        scope.launch {
            val builder = NotificationCompat.Builder(this@MusicService, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)

                .setSubText("Music Player")

                .setContentIntent(notifyPendingIntent)
                .addAction(R.drawable.ic_previous_notification, "Previous", getIntent(ACTION_PREVIOUS))
                .addAction(
                    if (MediaManager.isPause) R.drawable.ic_play_notification else R.drawable.ic_pause_notification,
                    if (MediaManager.isPause) "Resume" else "Pause",
                    if (MediaManager.isPause) getIntent(ACTION_RESUME) else getIntent(ACTION_PAUSE)
                )
                .addAction(R.drawable.ic_next_notification, "Next", getIntent(ACTION_NEXT))
                .addAction(R.drawable.ic_close_notification, "Close", getIntent(ACTION_CLOSE))
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2, 3))

            if(music == null){
                builder.setContentTitle(MusicDonwnloadedManager.currentMusicDownloaded!!.music)
                    .setContentText(MusicDonwnloadedManager.currentMusicDownloaded!!.artist)
                    .setLargeIcon(MusicDonwnloadedManager.currentMusicDownloaded!!.bitmap)
            }
            else{
                builder.setContentTitle(music.name)
                    .setContentText(music.artistName)
                    .setLargeIcon(music.image?.let { getBitmapFromURL(it) })
            }

            startForeground(1, builder.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaManager.mediaPlayer!!.stop()
        job.cancel()
    }

    private suspend fun getBitmapFromURL(url: String): Bitmap? {
        val loader = ImageLoader(this@MusicService)
        val request = ImageRequest.Builder(this@MusicService)
            .data(url)
            .allowHardware(false) // Disable hardware bitmaps.
            .build()

        val result = (loader.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    private fun getIntent(action: Int): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("action", action)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getService(this, action, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getService(this, action, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}