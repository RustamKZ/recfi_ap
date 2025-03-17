package com.example.films_shop.main_screen.objects

import kotlinx.serialization.Serializable

@Serializable
data class DetailsNavMovieObject (
    val id: String = "",
    val title: String = "",
    val type: String = "",
    val genre: String = "",
    val year: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val persons: String = "",
    val rating: Double = 0.0,
    val isFavorite: Boolean = false
)