package com.example.musicdownloader.networking

import com.example.musicdownloader.model.General
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Music
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("{option}?")
    suspend fun getMusics(
        @Path("option") option: String,
        @Query("offset") offset: Int,
        @Query("country") country: String = "vn"): General<Music>

    @GET("genre")
    suspend fun getGenres(): General<Genres>

}