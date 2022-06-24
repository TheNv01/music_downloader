package com.example.musicdownloader

import android.os.Build
import android.app.NotificationManager
import android.app.NotificationChannel
import com.proxglobal.proxads.ads.openads.ProxOpenAdsApplication

class App : ProxOpenAdsApplication() {

    companion object{
        const val CHANNEL_ID = "CHANNEL_MUSIC_APP"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        applicationContext?.let { SharedPreferencesManager.with(it) }
        Utils.initPath(this)
        SharedPreferencesManager.put( true, "in app")
    }

    override fun getOpenAdsId(): String = ""

    override fun getListTestDeviceId(): MutableList<String> {
        return mutableListOf("")
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build. VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, "Channel music",NotificationManager.IMPORTANCE_DEFAULT)

            channel.setSound(null, null)
            val manager = getSystemService(NotificationManager::class.java)

            manager?.createNotificationChannel(channel)
        }
    }


}