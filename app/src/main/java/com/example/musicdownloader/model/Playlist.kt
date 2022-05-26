package com.example.musicdownloader.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "playlist")
data class Playlist(
    val name: String,
    val musics: ArrayList<Music>,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
): Serializable
