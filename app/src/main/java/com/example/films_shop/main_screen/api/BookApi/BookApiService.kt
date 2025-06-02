package com.example.films_shop.main_screen.api.BookApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.http.GET
import retrofit2.http.Query


interface BookApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("startIndex") startIndex: Int = 0,
        @Query("langRestrict") lang: String = "ru",
        @Query("orderBy") orderBy: String = "relevance"
    ): BookResponse

    @GET("volumes")
    suspend fun searchBookByIsbn(
        @Query(value = "q") isbnQuery: String,
        @Query("key") apiKey: String
    ): BookResponse

//    @GET("volumes")
//    suspend fun searchBookByTitle(
//        @Query("q") titleQuery: String,
//        @Query("key") apiKey: String // Здесь ключ обязателен для большинства запросов
//    ): BookResponse

}

suspend fun searchBooksByIsbnList(isbnList: List<String>, bookApiService: BookApiService): List<BookResponse> = coroutineScope {
    val deferredList = isbnList.map { isbn ->
        async {
            try {
                bookApiService.searchBookByIsbn("isbn:$isbn", "AIzaSyAKHz0gmZ5IWWlvSGcw-ATX-8hMzm5dFJQ")
            } catch (e: Exception) {
                // Логируем ошибку или возвращаем заглушку
                // Например, можно возвращать пустой BookResponse или null
                null
            }
        }
    }

    deferredList.mapNotNull { it.await() }  // собираем только успешные ответы
}
