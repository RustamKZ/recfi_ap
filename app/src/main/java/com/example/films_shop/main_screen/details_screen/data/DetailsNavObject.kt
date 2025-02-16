package com.example.films_shop.main_screen.details_screen.data

import kotlinx.serialization.Serializable

@Serializable
data class DetailsNavObject(
    val title: String = "",
    val genre: String = "",
    val year: String = "",
    val director: String = "",
    val description: String = "",
    val imageUrl: String = "",
)
