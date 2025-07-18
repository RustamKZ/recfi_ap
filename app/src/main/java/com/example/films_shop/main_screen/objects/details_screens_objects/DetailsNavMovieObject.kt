package com.example.films_shop.main_screen.objects.details_screens_objects

import kotlinx.serialization.Serializable

@Serializable
data class DetailsNavMovieObject (
    val id: String = "",
    val tmdbId: Int = 0,
    val title: String = "",
    val type: String = "",
    val genre: String = "",
    val year: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val backdropUrl: String = "",
    val persons: String = "",
    val ratingKp: Double = 0.0,
    val ratingImdb: Double = 0.0,
    val votesKp: Int = 0,
    val votesImdb: Int = 0,
    val isFavorite: Boolean = false,
    val isBookMark: Boolean = false,
    val isRated: Boolean = false,
    val userRating: Int = 0
)