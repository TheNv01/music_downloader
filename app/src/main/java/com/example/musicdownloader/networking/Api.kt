package com.example.musicdownloader.networking

import com.example.musicdownloader.model.General
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Music
import retrofit2.Call
import retrofit2.http.GET

interface Api {

    @GET("music/popular?offset=0")
    suspend fun getPopulars(): General<Music>

    @GET("music/listened?offset=0")
    suspend fun getTopListened(): General<Music>

    @GET("music/download?offset=0")
    suspend fun getTopDownload(): General<Music>

    @GET("music/genres")
    suspend fun getGenres(): General<Genres>

}