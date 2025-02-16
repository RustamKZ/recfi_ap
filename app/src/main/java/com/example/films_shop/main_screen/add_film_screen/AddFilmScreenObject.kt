package com.example.films_shop.main_screen.add_film_screen

import kotlinx.serialization.Serializable

@Serializable
data class AddFilmScreenObject(
    val key: String = "",
    val title: String = "",
    val genre: String = "",
    val year: String = "",
    val director: String = "",
    val description: String = "",
    val imageUrl: String = ""
)