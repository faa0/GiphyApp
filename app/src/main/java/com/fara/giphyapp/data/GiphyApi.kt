package com.fara.giphyapp.data

import com.fara.giphyapp.data.model.GiphyResponse
import com.fara.giphyapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyApi {

    @GET("v1/gifs/trending")
    suspend fun getTrending(
        @Query("api_key")
        apiKey: String = API_KEY,
        @Query("limit")
        limit: Int,
        @Query("rating")
        rating: String = "g",
        @Query("offset")
        offset: Int
    ): Response<GiphyResponse>

    @GET("v1/gifs/search")
    suspend fun searchGifs(
        @Query("api_key")
        apiKey: String = API_KEY,
        @Query("q")
        q: String,
        @Query("limit")
        limit: Int,
        @Query("rating")
        rating: String = "g",
        @Query("offset")
        offset: Int,
        @Query("lang")
        lang: String = "en"
    ): Response<GiphyResponse>
}