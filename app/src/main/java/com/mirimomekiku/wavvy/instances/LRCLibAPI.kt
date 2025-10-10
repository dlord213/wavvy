package com.mirimomekiku.wavvy.instances

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class LRCLibOkResponse(
    val id: Number,
    val trackName: String,
    val artistName: String,
    val albumName: String,
    val duration: Long,
    val instrumental: Boolean,
    val plainLyrics: String,
    val syncedLyrics: String
)

data class LRCLibErrorResponse(
    val code: Number,
    val name: String,
    val message: String
)

interface LRCLibAPIService {
    @GET("api/get")
    suspend fun getLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String,
        @Query("album_name") albumName: String,
        @Query("duration") duration: Long
    ): Response<LRCLibOkResponse>
}

object LRCLibAPIRetrofitInstance {
    private const val BASE_URL = "https://lrclib.net/"

    val api: LRCLibAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LRCLibAPIService::class.java)
    }
}