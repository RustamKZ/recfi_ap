package com.example.films_shop.main_screen.objects.fav_screens_objects

import kotlinx.serialization.Serializable

@Serializable
data class FavMovieScreenDataObject (
    val uid: String = "",
    val email: String = ""
)