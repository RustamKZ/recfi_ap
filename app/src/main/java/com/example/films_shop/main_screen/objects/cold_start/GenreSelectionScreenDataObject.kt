package com.example.films_shop.main_screen.objects.cold_start
import kotlinx.serialization.Serializable

@Serializable
data class GenreSelectionScreenDataObject(
    val uid: String = "",
    val email: String = ""
)
