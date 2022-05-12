package com.example.musicdownloader.networking

import com.example.musicdownloader.model.General
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Music
import retrofit2.http.GET

interface Api {

    @GET("popular?offset=0&country=vn")
    suspend fun getPopulars(): General<Music>

    @GET("listened?offset=0&country=vn")
    suspend fun getTopListened(): General<Music>

    @GET("download?offset=0&country=vn")
    suspend fun getTopDownload(): General<Music>

    @GET("ranking?country=vn")
    suspend fun getTopRating(): General<Music>

    @GET("genre")
    suspend fun getGenres(): General<Genres>

}