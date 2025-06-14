package com.example.films_shop.main_screen.api.BookApi

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState

class BookPagingSource(
    private val apiService: BookApiService,
    private val authors: List<String>
) : PagingSource<Int, Book>() {

    private val pageSize = 5
    private val pagesPerAuthor = 3

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: 0
        val authorPageIndex = page % pagesPerAuthor
        val startIndex = authorPageIndex * pageSize

        val booksChunk = mutableListOf<Book>()

        for ((index, author) in authors.withIndex()) {
            val query = "inauthor:$author"
            try {
                val response = apiService.searchBooks(
                    query = query,
                    maxResults = pageSize,
                    startIndex = startIndex
                )

                val books = response.items?.mapNotNull { item ->
                    val isbn10 = item.volumeInfo.industryIdentifiers
                        ?.firstOrNull { it.type == "ISBN_10" }
                        ?.identifier

                    if (isbn10.isNullOrBlank()) return@mapNotNull null

                    val thumbnail = item.volumeInfo.imageLinks?.run {
                        extraLarge ?: large ?: medium ?: small ?: thumbnail ?: smallThumbnail
                    }
                    Log.d("TestImageBook", thumbnail?:"")
                    if (thumbnail.isNullOrEmpty()) return@mapNotNull null

                        Book(
                            id = item.id,
                            title = item.volumeInfo.title,
                            authors = item.volumeInfo.authors ?: listOf("Неизвестный автор"),
                            thumbnail = thumbnail,
                            publishedDate = item.volumeInfo.publishedDate,
                            description = item.volumeInfo.description,
                            isbn10 = isbn10,
                            publisher = item.volumeInfo.publisher,
                            pageCount = item.volumeInfo.pageCount,
                            categories = item.volumeInfo.categories,
                            averageRating = item.volumeInfo.averageRating,
                            ratingsCount = item.volumeInfo.ratingsCount,
                            language = item.volumeInfo.language
                        )
                } ?: emptyList()

                booksChunk.addAll(books)

            } catch (e: Exception) {
                // Игнорируем ошибки одного автора, продолжаем со следующими
            }
        }

        // Перемешиваем результат: например, можно просто случайно или по очереди
        val shuffledBooks = booksChunk.shuffled() // или customMix(booksByAuthor)

        val nextKey = if (authorPageIndex + 1 < pagesPerAuthor) page + 1 else null

        return LoadResult.Page(
            data = shuffledBooks,
            prevKey = if (page == 0) null else page - 1,
            nextKey = nextKey
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey?.plus(1) }
    }
}


