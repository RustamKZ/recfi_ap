package com.example.films_shop.main_screen.api

data class Movie(
    val id: String = "",
    val externalId: ExternalId?,
    val name: String? = "",
    val type: String? = "",
    val description: String? = "",
    val year: String? = "",
    val poster: Poster?,
    val backdrop: Backdrop?,
    val persons: List<Persons>?= emptyList(),
    val genres: List<Genre>?= emptyList(),
    val rating: Rating?,
    val votes: Votes?,
    val isFavorite: Boolean = false,
    val isBookMark: Boolean = false,
    val isRated: Boolean = false,
    val userRating: Int?
)

data class ExternalId(
    val tmdb: Int?
)

data class Poster(
    val url: String = ""
)

data class Backdrop(
    val url: String = ""
)

data class Rating(
    val kp: Double?,
    val imdb: Double?
)

data class Votes(
    val kp: Int?,
    val imdb: Int?
)

data class Genre(
    val name: String
)

data class Persons(
    val name: String = "",
    val photo: String = "",
    val profession: String = ""
)

data class KpImage(
    val url: String,
    val width: Int,
    val height: Int
)


//const val apiKey = ""
//const val apiKey = ""
//const val apiKey = ""
const val apiKey = ""
const val apiKeyBook = ""
