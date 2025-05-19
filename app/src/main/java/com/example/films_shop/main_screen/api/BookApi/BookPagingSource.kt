package com.example.films_shop.main_screen.api.BookApi

import androidx.paging.PagingSource
import androidx.paging.PagingState

class BookPagingSource(
    private val apiService: BookApiService,
    private val authors: List<String>
) : PagingSource<Int, Book>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: 0
        val pageSize = 10
        val pagesPerAuthor = 3  // сколько страниц книг подгружаем максимум на одного автора


        // Вычисляем индекс автора и индекс страницы внутри этого автора
        val authorIndex = page / pagesPerAuthor
        val authorPage = page % pagesPerAuthor
        val startIndex = authorPage * pageSize

        if (authorIndex >= authors.size) {
            // Если авторов больше не осталось — конец данных
            return LoadResult.Page(
                data = emptyList(),
                prevKey = if (page == 0) null else page - 1,
                nextKey = null
            )
        }

        val author = authors[authorIndex]

        return try {
            val query = "inauthor:$author"
            val response = apiService.searchBooks(query, maxResults = pageSize, startIndex = startIndex)

            val books = response.items
                ?.mapNotNull { item ->
                    val thumbnail = item.volumeInfo.imageLinks?.thumbnail
                    if (thumbnail.isNullOrEmpty()) null
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
