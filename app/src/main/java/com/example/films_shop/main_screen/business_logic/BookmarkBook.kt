package com.example.films_shop.main_screen.business_logic

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
    val userRating: Int = 0
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
        userRating = book.userRating
    )
}