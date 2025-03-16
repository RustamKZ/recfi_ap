package com.example.films_shop.main_screen.business_logic

import com.example.films_shop.main_screen.api.Movie

data class FavoriteMovie(
    val key: String = "",
    val name: String? = "",
    val year: String? = "",
    val posterUrl: String? = "",
    val description: String? = "",
    val genres: List<String>? = emptyList(),
    val persons: List<String>? = emptyList(),
    val rating: Double? = null
) {
    constructor(movie: Movie) : this(
        key = movie.id,
        name = movie.name,
        year = movie.year,
        posterUrl = movie.poster?.url,
        description = movie.description,
        genres = movie.genres?.map { it.name },
        persons = movie.persons?.map {it.name},
        rating = movie.rating?.kp
    )
}


