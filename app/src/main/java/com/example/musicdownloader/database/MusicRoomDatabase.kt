package com.example.musicdownloader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicdownloader.TypeConverter
import com.example.musicdownloader.database.dao.PlaylistDAO
import com.example.musicdownloader.model.Playlist

@Database(entities = [Playlist::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class MusicRoomDatabase : RoomDatabase() {

    abstract fun playlistDAO(): PlaylistDAO

    object MusicDatabaseBuilder{

        private var instance: MusicRoomDatabase? = null

        fun getInstance(context: Context): MusicRoomDatabase{
            if (instance == null){
                synchronized(MusicRoomDatabase::class){
                    if(instance == null){
                        instance = Room.databaseBuilder(context.applicationContext,
                            MusicRoomDatabase::class.java, "musicdatabase")
                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }
            return instance!!
        }
    }
}