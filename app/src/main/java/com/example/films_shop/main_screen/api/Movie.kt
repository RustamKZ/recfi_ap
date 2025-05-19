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
    val profession: String = ""
)


const val apiKey = "CS3QP35-CCE4F8D-N7APS7G-VAN76RX"
//const val apiKey = "MQ3ZZZE-KQT4R67-P2MFJBZ-JD20K49"
//const val apiKey = "WP6PH1H-X084H8Z-P8MZHP5-AXQ8A45"
//const val apiKey = "GDY53QV-B224Y8D-PGSDM5F-X3H8EKE"
