package com.example.films_shop.main_screen.objects

import kotlinx.serialization.Serializable

@Serializable
data class MovieScreenDataObject(
    val uid: String = "",
    val email: String = ""
)