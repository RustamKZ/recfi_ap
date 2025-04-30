package com.example.films_shop.main_screen.api.recomendations

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class RecommendationItem(
    val tmdbId: Int
)

data class RecommendationItemBooks(
    val isbn10: String
)


interface RecommendationApi {
    @GET("recommend/content/{film_id}")
    suspend fun getRecommendationsContentFilms(
        @Path("film_id") filmId: Int,
        @Query("top_n") topN: Int = 10
    ): List<RecommendationItem>

    @GET("recommend/content_series/{film_id}")
    suspend fun getRecommendationsContentSeries(
        @Path("film_id") filmId: Int,
        @Query("top_n") topN: Int = 10
    ): List<RecommendationItem>

    @GET("recommend/content_books/{isbn10}")
    suspend fun getRecommendationsContentBooks(
        @Path("isbn10") isbn10: String,
        @Query("top_n") topN: Int = 10
    ): List<RecommendationItemBooks>
}

