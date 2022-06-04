package com.example.musicdownloader.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "favorite")
data class Music(
    @PrimaryKey
    val id: String,
    val name: String?,
    val duration: Int?,
    val artistId: String?,
    val artistName: String?,
    val artistIdstr: String?,
    val albumName: String?,
    val albumId: String?,
    val licenseCcurl: String?,
    val position: Int?,
    val releaseDate: String?,
    val albumImage: String?,
    val audio: String?,
    val audioDownload: String?,
    val prourl: String?,
    val shorturl: String?,
    val shareurl: String?,
    val image: String?,
    val audioDownloadAllowed: Boolean?,
    val source: String?,
    var isFavorite: Boolean = false,
    var isAddToPlaylist: Boolean = false
): Serializable
