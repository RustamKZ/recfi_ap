package com.example.films_shop.main_screen.objects.details_screens_objects

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
    val isFavorite: Boolean = false,
    val isBookmark: Boolean = false,
    val isRated: Boolean = false,
    val userRating: Int = 0,
    val publisher: String? = "",
    val pageCount: Int? = 0,
    val categories: String = "",
    val averageRating: Double? = 0.0,
    val ratingsCount: Int? = 0,
    val language: String? = ""
)