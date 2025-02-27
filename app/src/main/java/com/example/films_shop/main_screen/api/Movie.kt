package com.example.films_shop.main_screen.api

data class Movie(
    val id: String = "",
    val name: String? = "",
    val description: String? = "",
    val year: String? = "",
    val poster: Poster?,
    val genres: List<Genre>?,
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

fun Movie.copySafe(
    id: String = this.id,
    name: String = this.name ?: "",
    description: String? = this.description,
    year: String? = this.year,
    poster: Poster? = this.poster ?: Poster(""), // Предотвращаем null
    genres: List<Genre>? = this.genres ?: emptyList(), // Предотвращаем null
    rating: Rating? = this.rating ?: Rating(0.0), // Предотвращаем null
    isFavorite: Boolean = this.isFavorite
): Movie {
    return Movie(id, name, description, year, poster, genres, rating, isFavorite)
}


const val apiKey = "CS3QP35-CCE4F8D-N7APS7G-VAN76RX"
