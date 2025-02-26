package com.example.films_shop.main_screen.api.details

import kotlinx.serialization.Serializable

@Serializable
data class DetailsNavMovieObject (
    val title: String = "",
    val genre: String = "",
    val year: String = "",
    val director: String = "",
    val description: String = "",
    val imageUrl: String = "",
)