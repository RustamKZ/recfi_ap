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
            "persons", "id", "year", "description", "rating", "votes","genres", "poster","backdrop", "name", "type", "externalId"
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
            "persons", "id", "year", "description", "rating", "votes", "genres", "poster", "backdrop","name", "type", "externalId"
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
            "persons", "id", "year", "description", "rating", "votes", "genres", "poster", "backdrop","name", "type", "externalId"
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
            "persons", "id", "year", "description", "rating", "votes", "genres", "poster","backdrop", "name", "type", "externalId"
        )
    ): MovieResponse

    @GET("v1.4/movie")
    suspend fun getMovieByTmdbId(
        @Header("X-API-KEY") apiKey: String,
        @Query("externalId.tmdb") id: String,
        @Query("selectFields") selectFields: List<String> = listOf(
            "persons", "id", "year", "description", "rating", "votes", "genres", "poster","backdrop", "name", "type", "externalId"
        )
    ): MovieResponse

    @GET("v1.4/image")
    suspend fun getMovieImages(
        @Header("X-API-KEY") apiKey: String,
        @Query("movieId") movieId: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ImageResponse

    @GET("v1.4/movie/search")
    suspend fun searchMoviesByName(
        @Header("X-API-KEY") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): MovieResponse

    @GET("v1.4/movie")
    suspend fun getMoviesByGenres(
        @Header("X-API-KEY") apiKey: String,
        @Query("genres.name") genres: List<String>, // ← фильтрация по жанрам
        @Query("type") type: String,
        @Query("limit") limit: Int = 30,
        @Query("page") page: Int = 1,
        @Query("selectFields") selectFields: List<String> = listOf(
            "persons", "id", "year", "description", "rating", "votes", "genres", "poster","backdrop", "name", "type", "externalId"
        ),
        @Query("sortField") sortField: String = "rating.kp",
        @Query("sortType") sortType: String = "-1",
    ): MovieResponse



    data class ImageResponse(
        val docs: List<KpImage>
    )

}
