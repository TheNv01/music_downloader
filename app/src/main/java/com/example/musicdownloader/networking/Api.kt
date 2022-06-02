package com.example.musicdownloader.networking

import com.example.musicdownloader.model.General
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Music
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("{option}?")
    suspend fun getMusics(
        @Path("option") option: String,
        @Query("offset") offset: Int,
        @Query("country") country: String = "vn"): General<List<Music>>

    @GET("url?")
    suspend fun getLinkSourceSc(
        @Query("id") id: String): General<String>

    @GET("genre")
    suspend fun getGenres(): General<List<Genres>>

    @GET("search?")
    suspend fun searchMusic(
        @Query("offset") offset: Int,
        @Query("query") country: String = ""): General<List<Music>>


}