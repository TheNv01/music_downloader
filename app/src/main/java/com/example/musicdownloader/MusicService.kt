package com.example.musicdownloader

import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.musicdownloader.manager.*
import com.example.musicdownloader.model.MessageEvent
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.MusicDownloaded
import com.example.musicdownloader.networking.Services
import com.example.musicdownloader.view.MainActivity
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
                    if(MusicDonwnloadedManager.currentMusicDownloaded == null) {
                        MusicManager.getCurrentMusic()?.let { startMusic(it) }
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
                            Log.d("downloaded", MusicDonwnloadedManager.musicsDownloaded.size.toString())
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
        MediaManager.resetMedia()
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

    private fun pushNotification(music: Music ?= null) {
        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.putExtra("from notification", 1)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(1,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        GlobalScope.launch {
            val builder = NotificationCompat.Builder(this@MusicService, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)

                .setSubText("Music Player")

                .setContentIntent(resultPendingIntent)
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

            if(music == null){
                builder.setContentTitle(MusicDonwnloadedManager.currentMusicDownloaded!!.music)
                    .setContentText(MusicDonwnloadedManager.currentMusicDownloaded!!.artist)
                    .setLargeIcon(MusicDonwnloadedManager.currentMusicDownloaded!!.bitmap)
            }
            else{
                builder.setContentTitle(music.name)
                    .setContentText(music.artistName)
                    .setLargeIcon(getBitmapFromURL(music.image!!))
            }

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