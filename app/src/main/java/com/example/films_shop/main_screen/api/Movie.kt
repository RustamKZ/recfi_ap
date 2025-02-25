package com.example.films_shop.main_screen.api

data class Movie(
    val id: Int,
    val name: String,
    val description: String?,
    val year: Int?,
    val poster: Poster?,
    val rating: Rating?
)

data class Poster(
    val url: String
)

data class Rating(
    val kp: Double?
)


const val apiKey = "CS3QP35-CCE4F8D-N7APS7G-VAN76RX"
