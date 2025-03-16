package com.example.films_shop.main_screen.business_logic

import com.example.films_shop.main_screen.api.BookApi.Book
import com.example.films_shop.main_screen.api.Movie

data class FavoriteBook(
    val key: String = "",
    val title: String? = "",
    val authors: List<String>? = emptyList(),
    val thumbnail: String? = "",
    val publishedDate: String? = "",
    val description: String? = "",
) {
    constructor(book: Book) : this(
        key = book.id,
        title = book.title,
        authors = book.authors,
        thumbnail = book.thumbnail,
        publishedDate = book.publishedDate,
        description = book.description
    )
}