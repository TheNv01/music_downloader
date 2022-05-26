package com.example.musicdownloader

import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Option
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverter {

    @androidx.room.TypeConverter
    fun fromMusic(music: Music): String?{
        return Gson().toJson(music)
    }

    @androidx.room.TypeConverter
    fun toMusic(music: String): Music?{
        return Gson().fromJson(music, Music::class.java)
    }
    @androidx.room.TypeConverter
    fun fromListMusic(list: ArrayList<Music>?): String?{
        val listType = object : TypeToken<ArrayList<Music>>() {}.type
        return Gson().toJson(list, listType)

    }

    @androidx.room.TypeConverter
    fun toListMusic(list: String): ArrayList<Music>? {
        val listType = object : TypeToken<ArrayList<Music>>() {}.type
        return Gson().fromJson<ArrayList<Music>>(list, listType)

    }

}