package com.example.films_shop.main_screen.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MovieApiService {
    @GET("v1.4/movie")
    suspend fun getTop250Movies(
        @Header("X-API-KEY") apiKey: String,
        @Query("page") page: Int = 2,
        @Query("limit") limit: Int = 250,
        @Query("selectFields") selectFields: List<String> = listOf(
            "persons", "id", "year", "description", "rating", "genres", "poster","backdrop", "name", "type", "externalId"
        ),
        @Query("sortField") sortField: String = "top250",
        @Query("sortType") sortType: String = "-1",
        @Query("type") type: String = "movie"
    ): MovieResponse

    @GET("v1.4/movie")
    suspend fun getTop250TvSeries(
        @Header("X-API-KEY") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 250,
        @Query("selectFields") selectFields: List<String> = listOf(
            "persons", "id", "year", "description", "rating", "genres", "poster", "backdrop","name", "type", "externalId"
        ),
        @Query("sortField") sortField: String = "top250",
        @Query("sortType") sortType: String = "-1",
        @Query("type") type: String = "tv-series"
    ): MovieResponse

    @GET("v1.4/movie")
    suspend fun getTop250Cartoons(
        @Header("X-API-KEY") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 250,
        @Query("selectFields") selectFields: List<String> = listOf(
            "persons", "id", "year", "description", "rating", "genres", "poster", "backdrop","name", "type", "externalId"
        ),
        @Query("sortField") sortField: String = "top250",
        @Query("sortType") sortType: String = "-1",
        @Query("type") type: String = "cartoon"
    ): MovieResponse

    @GET("v1.4/movie")
    suspend fun getMoviesByTmdbIds(
        @Header("X-API-KEY") apiKey: String,
        @Query("externalId.tmdb") tmdbIds: List<Int>,
        @Query("selectFields") selectFields: List<String> = listOf(
            "persons", "id", "year", "description", "rating", "genres", "poster","backdrop", "name", "type", "externalId"
        )
    ): MovieResponse
}
