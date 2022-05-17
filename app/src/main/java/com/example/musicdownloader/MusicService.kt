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
import com.example.musicdownloader.manager.MediaManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.manager.RepeatStatus
import com.example.musicdownloader.model.MessageEvent
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.networking.Services
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus

class MusicService : Service() {

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
                    MusicManager.getCurrentMusic()?.let { startMusic(it) }
                }
                ACTION_RESUME -> {
                    MediaManager.resumeMedia()
                }
                ACTION_PAUSE -> {
                    MediaManager.pauseMedia()
                }
                ACTION_PREVIOUS ->{
                    MusicManager.previousMusic()
                }
                ACTION_NEXT ->{
                    MusicManager.nextMusic()
                }
                ACTION_CLOSE -> {
                    stopSelf()
                    MusicManager.setCurrentMusic(null)
                }
            }
            EventBus.getDefault().post(MessageEvent(action))
            MusicManager.getCurrentMusic()?.let { pushNotification(it) }
        }
        return START_NOT_STICKY
    }

    private fun startMusic(music: Music) {
        MediaManager.resetMedia()
        GlobalScope.launch {
            MediaManager.playMusic(getLinkAudio(music)){
                when(MusicManager.getRepeatStatus()){
                    RepeatStatus.NoRepeat ->{
                        if(MusicManager.getIndexOfCurrentMusic() == MusicManager.getSizeMusicList() -1){
                            EventBus.getDefault().post(MessageEvent(ACTION_PAUSE))
                            MediaManager.pauseMedia()
                        }
                        else{
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
    }

    private suspend fun getLinkAudio(music: Music): String{
        return if(music.source.equals("SC")){
            Services.retrofitService.getLinkSourceSc(music.id).data
        } else{
            music.audio!!
        }
    }

    private fun pushNotification(music: Music) {

        GlobalScope.launch {
            val builder = NotificationCompat.Builder(this@MusicService, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(getBitmapFromURL(music.image!!))
                .setSubText("Music Player")
                .setContentTitle(music.name)
                .setContentText(music.artistName)
                .addAction(R.drawable.ic_previous_notification, "Previous", getIntent(ACTION_PREVIOUS))
                .addAction(
                    if (MediaManager.isPause()) R.drawable.ic_play_notification else R.drawable.ic_pause_notification,
                    if (MediaManager.isPause()) "Resume" else "Pause",
                    if (MediaManager.isPause()) getIntent(ACTION_RESUME) else getIntent(ACTION_PAUSE)
                )
                .addAction(R.drawable.ic_next_notification, "Next", getIntent(ACTION_NEXT))
                .addAction(R.drawable.ic_close_notification, "Close", getIntent(ACTION_CLOSE))
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2, 3))

            startForeground(1, builder.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaManager.resetMedia()
    }

    private suspend fun getBitmapFromURL(url: String): Bitmap? {

        val loading = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(url)
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return  (result as BitmapDrawable).bitmap
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