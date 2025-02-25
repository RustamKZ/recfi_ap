package com.example.films_shop.main_screen.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MovieApiService {
    @GET("v1.4/movie")
    suspend fun getPopularMovies(
        @Header("X-API-KEY") apiKey: String,
        @Query("page") page: Int = 2,
        @Query("limit") limit: Int = 250,
        @Query("sortField") sortField: String = "rating.kp",
        @Query("sortType") sortType: String = "-1",
        @Query("rating.kp") rating: String = "7-10"
    ): MovieResponse
}
