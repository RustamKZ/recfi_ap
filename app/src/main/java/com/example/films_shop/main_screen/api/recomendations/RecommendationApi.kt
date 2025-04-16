package com.example.films_shop.main_screen.api.recomendations

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class RecommendationItem(
    val tmdbId: Int
)


interface RecommendationApi {
    @GET("recommend/content/{film_id}")
    suspend fun getRecommendations(
        @Path("film_id") filmId: Int,
        @Query("top_n") topN: Int = 10
    ): List<RecommendationItem>
}

