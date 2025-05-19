package com.example.films_shop.main_screen.business_logic.data_classes

import com.example.films_shop.main_screen.api.Movie

data class RatedMovie(
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
    val ratingKp: Double? = null,
    val ratingImdb: Double? = null,
    val votesKp: Int? = null,
    val votesImdb: Int? = null,
    val isBookMark: Boolean = false,
    val isFavorite: Boolean = false,
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
        ratingKp = movie.rating?.kp,
        ratingImdb = movie.rating?.imdb,
        votesKp = movie.votes?.kp,
        votesImdb = movie.votes?.imdb,
        isBookMark = movie.isBookMark,
        isFavorite = movie.isFavorite,
        userRating = movie.userRating
    )
}


