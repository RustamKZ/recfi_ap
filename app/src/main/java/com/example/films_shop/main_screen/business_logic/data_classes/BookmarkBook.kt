package com.example.films_shop.main_screen.business_logic.data_classes

import com.example.films_shop.main_screen.api.BookApi.Book

data class BookmarkBook(
    val key: String = "",
    val isbn10: String = "",
    val title: String? = "",
    val authors: List<String>? = emptyList(),
    val thumbnail: String? = "",
    val publishedDate: String? = "",
    val description: String? = "",
    val isFavorite: Boolean = false,
    val isRated: Boolean = false,
    val userRating: Int = 0,
    val publisher: String? = "",
    val pageCount: Int? = 0,
    val categories: List<String>? = emptyList(),
    val averageRating: Double? = 0.0,
    val ratingsCount: Int? = 0,
    val language: String? = ""
) {
    constructor(book: Book) : this(
        key = book.id,
        isbn10 = book.isbn10,
        title = book.title,
        authors = book.authors,
        thumbnail = book.thumbnail,
        publishedDate = book.publishedDate,
        description = book.description,
        isFavorite = book.isFavorite,
        isRated = book.isRated,
        userRating = book.userRating,
        publisher = book.publisher,
        pageCount = book.pageCount,
        categories = book.categories,
        averageRating = book.averageRating,
        ratingsCount = book.ratingsCount,
        language = book.language
    )
}