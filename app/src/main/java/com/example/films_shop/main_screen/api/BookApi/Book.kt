package com.example.films_shop.main_screen.api.BookApi

data class Book(
    val id: String,
    val title: String,
    val authors: List<String>?,
    val thumbnail: String?,
    val publishedDate: String?,
    val description: String?,
    val isFavorite: Boolean = false
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val description: String?,
    val publishedDate: String?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String?
)

data class BookResponse(
    val items: List<BookItem>?
)

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)
