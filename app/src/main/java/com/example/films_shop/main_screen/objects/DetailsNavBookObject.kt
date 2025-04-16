package com.example.films_shop.main_screen.objects

import kotlinx.serialization.Serializable

@Serializable
data class DetailsNavBookObject (
    val id: String = "",
    val isbn10: String = "",
    val title: String = "",
    val authors: String = "",
    val thumbnail: String = "",
    val publishedDate: String = "",
    val description: String = "",
    val isFavorite: Boolean = false
)