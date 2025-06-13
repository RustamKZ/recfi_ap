package com.example.films_shop.main_screen.objects.rec_objects.genre_authors
import kotlinx.serialization.Serializable

@Serializable
data class RecBookScreenAuthorObject(
    val uid: String = "",
    val email: String = ""
)