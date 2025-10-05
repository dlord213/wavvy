package com.mirimomekiku.wavvy.instances

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// - Songs - //
data class GeniusSearchResponse(
    val response: GeniusHits
)

data class GeniusHits(
    val hits: List<GeniusHit>
)

data class GeniusHit(
    val result: GeniusSong
)

data class GeniusSong(
    val id: Long,
    val full_title: String,
    val title: String,
    val artist_names: String,
    val primary_artist: GeniusArtist,
    val url: String,
    val header_image_url: String,
    val header_image_thumbnail_url: String
)

data class GeniusArtist(
    val id: Long,
    val name: String
)
// - Songs - //

// - Artist - //
data class GeniusArtistResponse(
    val response: GeniusArtistResult
)

data class GeniusArtistResult(
    val artist: GeniusArtistDetails
)

data class GeniusArtistDetails(
    val id: Long,
    val name: String,
    val header_image_url: String,
    val image_url: String,
    val description: GeniusDescription
)

data class GeniusDescription(
    val dom: String,
    val plain: String
)
// - Artist - //

fun extractPrimaryArtist(artistNames: String?): String {
    if (artistNames.isNullOrBlank()) return ""
    return artistNames
        .split(",", "&", "feat.", "Feat.", "FEAT.")
        .first()
        .trim()
}

interface GeniusAPIService {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Header("Authorization") token: String
    ): GeniusSearchResponse

    @GET("artists/{id}")
    suspend fun getArtist(
        @retrofit2.http.Path("id") artistId: Long,
        @Query("text_format") textFormat: String = "plain",
        @Header("Authorization") token: String
    ): GeniusArtistResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.genius.com/"

    val api: GeniusAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeniusAPIService::class.java)
    }
}