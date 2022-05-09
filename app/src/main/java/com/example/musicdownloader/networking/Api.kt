package com.example.musicdownloader.networking

import com.example.musicdownloader.model.General
import com.example.musicdownloader.model.Music
import retrofit2.Call
import retrofit2.http.GET

interface Api {

    @GET("music/popular?offset=0")
    suspend fun getPopulars(): General<Music>
}