package com.example.musicdownloader

import android.content.Context
import android.os.Environment
import java.io.File

object Utils {
    var PATH = ""
    var pathRingtone = ""
    fun initPath(context: Context) {
        PATH = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString().plus("/music downloader")).path
        pathRingtone = context.getExternalFilesDir(Environment.DIRECTORY_RINGTONES).toString()
    }
}