package com.example.films_shop.main_screen.objects.rec_objects
import kotlinx.serialization.Serializable

@Serializable
data class CustomDatasetBooksObject(
    val uid: String = "",
    val email: String = ""
)