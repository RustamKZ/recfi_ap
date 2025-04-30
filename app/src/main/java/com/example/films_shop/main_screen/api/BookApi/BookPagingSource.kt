package com.example.films_shop.main_screen.api.BookApi

import androidx.paging.PagingSource
import androidx.paging.PagingState

class BookPagingSource(
    private val apiService: BookApiService,
    private val query: String
) : PagingSource<Int, Book>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: 0
        return try {
            val query = "inauthor:Ремарк"
            val response = apiService.searchBooks(query, maxResults = 10, startIndex = page * 10)

            val books = response.items
                ?.mapNotNull { item ->  // Используем mapNotNull для двойной фильтрации
                    val thumbnail = item.volumeInfo.imageLinks?.thumbnail
                    if (thumbnail.isNullOrEmpty()) null  // Отбрасываем книги без обложки
                    else {
                        val isbn10 = item.volumeInfo.industryIdentifiers
                            ?.firstOrNull { it.type == "ISBN_10" }
                            ?.identifier ?: "Неизвестно"

                        Book(
                            id = item.id,
                            title = item.volumeInfo.title,
                            authors = item.volumeInfo.authors ?: listOf("Неизвестный автор"),
                            thumbnail = thumbnail,
                            publishedDate = item.volumeInfo.publishedDate,
                            description = item.volumeInfo.description,
                            isbn10 = isbn10
                        )
                    }
                } ?: emptyList()

            LoadResult.Page(
                data = books,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (books.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey?.plus(1) }
    }
}
