package com.example.films_shop.main_screen.api

data class Movie(
    val id: String = "",
    val name: String? = "",
    val type: String? = "",
    val description: String? = "",
    val year: String? = "",
    val poster: Poster?,
    val persons: List<Persons>?= emptyList(),
    val genres: List<Genre>?= emptyList(),
    val rating: Rating?,
    val isFavorite: Boolean = false
)

data class Poster(
    val url: String = ""
)

data class Rating(
    val kp: Double?
)

data class Genre(
    val name: String
)

data class Persons(
    val name: String = "",
    val profession: String = ""
)


const val apiKey = "CS3QP35-CCE4F8D-N7APS7G-VAN76RX"
