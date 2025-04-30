package com.example.films_shop.main_screen.business_logic

import com.example.films_shop.main_screen.api.Movie

data class BookmarkMovie(
    val key: String = "",
    val tmdbId: Int? = null,
    val name: String? = "",
    val type: String? = "",
    val year: String? = "",
    val posterUrl: String? = "",
    val backdropUrl: String? = "",
    val description: String? = "",
    val genres: List<String>? = emptyList(),
    val persons: List<String>? = emptyList(),
    val rating: Double? = null,
    val isFavorite: Boolean = false,
    val isRated: Boolean = false,
    val userRating: Int? = 0
) {
    constructor(movie: Movie) : this(
        key = movie.id,
        tmdbId = movie.externalId?.tmdb,
        name = movie.name,
        type = movie.type,
        year = movie.year,
        posterUrl = movie.poster?.url,
        backdropUrl = movie.backdrop?.url,
        description = movie.description,
        genres = movie.genres?.map { it.name },
        persons = movie.persons?.map {it.name},
        rating = movie.rating?.kp,
        isFavorite = movie.isFavorite,
        isRated = movie.isRated,
        userRating = movie.userRating
    )
}
