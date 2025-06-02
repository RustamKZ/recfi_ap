package com.example.films_shop.main_screen.api.BookApi
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
}