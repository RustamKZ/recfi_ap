package com.example.films_shop.main_screen.api.BookApi

data class Book(
    val id: String,
    val isbn10: String,
    val title: String,
    val authors: List<String>?,
    val thumbnail: String?,
    val publishedDate: String? = "Неизвестно",
    val description: String?,
    val isFavorite: Boolean = false,
    val isBookMark: Boolean = false,
    val isRated: Boolean = false,
    val userRating: Int = -1,

    // Новые поля
    val publisher: String?,
    val pageCount: Int?,
    val categories: List<String>?,
    val averageRating: Double?,
    val ratingsCount: Int?,
    val language: String?
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val description: String?,
    val publishedDate: String?,
    val imageLinks: ImageLinks?,
    val industryIdentifiers: List<IndustryIdentifier>?,

    // Новые поля
    val publisher: String?,
    val pageCount: Int?,
    val categories: List<String>?,
    val averageRating: Double?,
    val ratingsCount: Int?,
    val language: String?
)

data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

data class ImageLinks(
    val smallThumbnail: String?,
    val thumbnail: String?,
    val small: String?,
    val medium: String?,
    val large: String?,
    val extraLarge: String?
)

data class BookResponse(
    val items: List<BookItem>?
)

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)
