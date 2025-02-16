package com.example.films_shop.main_screen.data

data class Film(
    val key: String = "",
    val title: String = "",
    val genre: String = "",
    val year: String = "",
    val director: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val isFavorite: Boolean = false
)