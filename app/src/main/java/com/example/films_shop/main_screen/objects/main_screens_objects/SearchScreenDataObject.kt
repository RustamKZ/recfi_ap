package com.example.films_shop.main_screen.objects.main_screens_objects
import kotlinx.serialization.Serializable

@Serializable
data class SearchScreenDataObject(
    val uid: String = "",
    val email: String = "",
    val showLoadingAnimation: Boolean = false
)