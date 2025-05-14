package com.example.films_shop.main_screen.api.recomendations

import ContentType
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.films_shop.main_screen.api.Backdrop
import com.example.films_shop.main_screen.api.BookApi.Book
import com.example.films_shop.main_screen.api.BookApi.RetrofitInstanceBooks
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.Poster
import com.example.films_shop.main_screen.api.RetrofitInstance
import com.example.films_shop.main_screen.api.apiKey
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RecommendationViewModel : ViewModel() {

    private val _tmdbRecommendationIds = mutableStateOf<List<Int>>(emptyList())

    private val _recommendationMovies = mutableStateOf<List<Movie>>(emptyList())
    val recommendationMovies: State<List<Movie>> = _recommendationMovies

    //Коллаборативная фильтрация
    private val _tmdbCollabRecommendationIdsMovies = mutableStateOf<List<Int>>(emptyList())
    private val _recommendationCollabMovies = mutableStateOf<List<Movie>>(emptyList())
    val recommendationCollabMovies: State<List<Movie>> = _recommendationCollabMovies

    private val _tmdbCollabRecommendationIdsCartoon = mutableStateOf<List<Int>>(emptyList())
    private val _recommendationCollabCartoon = mutableStateOf<List<Movie>>(emptyList())
    val recommendationCollabCartoon: State<List<Movie>> = _recommendationCollabCartoon

    private val _tmdbCollabRecommendationIdsTvSeries = mutableStateOf<List<Int>>(emptyList())
    private val _recommendationCollabTvSeries = mutableStateOf<List<Movie>>(emptyList())
    val recommendationCollabTvSeries: State<List<Movie>> = _recommendationCollabTvSeries
    //Коллаборативная фильтрация

    private val _isbn10RecommendationIds = mutableStateOf<List<String>>(emptyList())

    private val _recommendationBooks = mutableStateOf<List<Book>>(emptyList())
    val recommendationBooks: State<List<Book>> = _recommendationBooks

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // Получение рекомендаций (TMDB ID) по ID фильма
    fun fetchRecommendations(filmId: Int, type: String) {
        if (filmId == 0) {
            _error.value = "Некорректный ID фильма"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Получаем рекомендации (ID фильмов из TMDB)
                Log.d("RecommendationVM", "Получены ID: $filmId")
                val response = when (type) {
                    "cartoon" -> {
                        ApiClient.api.getRecommendationsContentFilms(filmId)
                    }
                    "movie" -> {
                        ApiClient.api.getRecommendationsContentFilms(filmId)
                    }
                    "tv-series" -> {
                        ApiClient.api.getRecommendationsContentSeries(filmId)
                    }

                    else -> {ApiClient.api.getRecommendationsContentFilms(filmId)}
                }
                val tmdbIds = response.map { it.tmdbId }
                _tmdbRecommendationIds.value = tmdbIds

                Log.d("RecommendationVM", "Получены ID рекомендаций: $tmdbIds")
                // Проверяем, есть ли рекомендации
                if (tmdbIds.isEmpty()) {
                    _recommendationMovies.value = emptyList()
                    _isLoading.value = false
                    return@launch
                }

                // Теперь запрашиваем информацию о фильмах по этим TMDB ID
                fetchMoviesByTmdbIds(tmdbIds)

            } catch (e: Exception) {
                Log.e("RecommendationVM", "Ошибка при получении рекомендаций", e)
                _error.value = "Ошибка получения рекомендаций: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun fetchCollabRecommendationsFilmsCartoonSeries(
        ratings: Map<String, Int>,
        type: String,
        nRecommendations: Int = 10
    ) {
        Log.d("Debug", "Fetching collab recommendations for type: $type, ratings: $ratings")
        if (ratings.isEmpty()) {
            _error.value = "Не передано ни одной оценки"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = when (type) {
                    "cartoon", "movie" -> {
                        ApiClient.api.getCollaborativeRecommendationsFilms(
                            nRecommendations = nRecommendations,
                            ratings = ratings
                        )
                    }
                    "tv-series" -> {
                        ApiClient.api.getCollaborativeRecommendationsSeries(ratings)
                    }
                    else -> {
                        _error.value = "Неизвестный тип контента"
                        _isLoading.value = false
                        return@launch
                    }
                }
                val tmdbIds = response.mapNotNull { it.toIntOrNull() }
                when (type) {
                    "movie" -> {
                        _tmdbCollabRecommendationIdsMovies.value = tmdbIds
                    }
                    "cartoon" -> {
                        _tmdbCollabRecommendationIdsCartoon.value = tmdbIds
                    }
                    "tv-series" -> {
                        _tmdbCollabRecommendationIdsTvSeries.value = tmdbIds
                    }
                }

                Log.d("RecommendationVM", "Коллаб рекомендованные ID: $tmdbIds")
                // Проверяем, есть ли рекомендации
                if (tmdbIds.isEmpty()) {
                    _recommendationMovies.value = emptyList()
                    _isLoading.value = false
                    return@launch
                }

                // Теперь запрашиваем информацию о фильмах по этим TMDB ID
                fetchMoviesByTmdbIdsCollab(tmdbIds, type)

            } catch (e: Exception) {
                Log.e("RecommendationVM", "Ошибка при получении коллаб. рекомендаций", e)
                _error.value = "Ошибка получения рекомендаций: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun fetchRecommendationsBooks(isbn10: String, authors: String) {
        if (isbn10 == "") {
            _error.value = "Некорректный isbn10 книги"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Получаем рекомендации (ID фильмов из TMDB)
                Log.d("RecommendationBooks", "Получены isbn10: $isbn10")
                val response = ApiClient.api.getRecommendationsContentBooks(isbn10)

                val isbn10Ids = response.map { it.isbn10 }
                _isbn10RecommendationIds.value = isbn10Ids

                Log.d("RecommendationBooks", "Получены isbn10 рекомендаций: $isbn10Ids")
                // Проверяем, есть ли рекомендации
                if (isbn10Ids.isEmpty()) {
                    _recommendationMovies.value = emptyList()
                    _isLoading.value = false
                    getSameBooksFromGoogleApi(isbn10, authors)
                    return@launch
                }

                // Теперь запрашиваем информацию о фильмах по этим TMDB ID
                fetchBooksByIsbn10(isbn10Ids)

            } catch (e: Exception) {
                Log.e("RecommendationVM", "Ошибка при получении рекомендаций", e)
                _error.value = "Ошибка получения рекомендаций: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Получение деталей фильмов по TMDB ID
    private suspend fun fetchMoviesByTmdbIds(tmdbIds: List<Int>) {
        try {
            val response = RetrofitInstance.api.getMoviesByTmdbIds(
                apiKey = apiKey,
                tmdbIds = tmdbIds
            )

            Log.d("RecommendationVM", "Получены детали фильмов: ${response.docs.size}")

            // Обрабатываем полученные фильмы так же, как в MovieViewModel
            val updatedMovies = response.docs.map { movie ->
                movie.copy(
                    poster = if (movie.poster?.url != null) {
                        movie.poster.copy(url = movie.poster.url)
                    } else {
                        Poster("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg")
                    },
                    backdrop = when {
                        // Проверяем наличие backdrop и что его url не null
                        movie.backdrop != null && movie.backdrop.url != null ->
                            movie.backdrop.copy(url = movie.backdrop.url)
                        // Если есть постер и его url не null, создаем Backdrop
                        movie.poster != null && movie.poster.url != null ->
                            Backdrop(movie.poster.url)
                        // Иначе используем дефолтную ссылку
                        else ->
                            Backdrop("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg")
                    },
                    persons = movie.persons?.filter { it.profession == "режиссеры" } ?: emptyList()
                )
            }

            // Фильтруем фильмы без названия
            val filteredMovies = updatedMovies.filter { it.name != null }

            _recommendationMovies.value = filteredMovies

        } catch (e: IOException) {
            Log.e("RecommendationVM", "Ошибка сети при получении деталей фильмов", e)
            _error.value = "Ошибка сети. Проверьте подключение к интернету."
        } catch (e: HttpException) {
            Log.e("RecommendationVM", "HTTP ошибка при получении деталей фильмов: ${e.code()}", e)
            _error.value = "Ошибка сервера: ${e.code()}"
        } catch (e: Exception) {
            Log.e("RecommendationVM", "Непредвиденная ошибка при получении деталей фильмов", e)
            _error.value = "Произошла ошибка: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    // Получение деталей фильмов по TMDB ID
    private suspend fun fetchMoviesByTmdbIdsCollab(tmdbIds: List<Int>, type: String) {
        try {
            val response = RetrofitInstance.api.getMoviesByTmdbIds(
                apiKey = apiKey,
                tmdbIds = tmdbIds
            )
            when (type) {
                "movie" -> {
                    Log.d("RecommendationVM", "Получены детали фильмов: ${response.docs.size}")
                }
                "cartoon" -> {
                    Log.d("RecommendationVM", "Получены детали мультфильмов: ${response.docs.size}")
                }
                "tv-series" -> {
                    Log.d("RecommendationVM", "Получены детали сериалов: ${response.docs.size}")
                }
            }

            val updatedMovies = response.docs.map { movie ->
                movie.copy(
                    poster = if (movie.poster?.url != null) {
                        movie.poster.copy(url = movie.poster.url)
                    } else {
                        Poster("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg")
                    },
                    backdrop = when {
                        // Проверяем наличие backdrop и что его url не null
                        movie.backdrop != null && movie.backdrop.url != null ->
                            movie.backdrop.copy(url = movie.backdrop.url)
                        // Если есть постер и его url не null, создаем Backdrop
                        movie.poster != null && movie.poster.url != null ->
                            Backdrop(movie.poster.url)
                        // Иначе используем дефолтную ссылку
                        else ->
                            Backdrop("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg")
                    },
                    persons = movie.persons?.filter { it.profession == "режиссеры" } ?: emptyList()
                )
            }

            // Фильтруем фильмы без названия
            val filteredMovies = updatedMovies.filter { it.name != null }
            when (type) {
                "movie" -> {
                    _recommendationCollabMovies.value = filteredMovies
                }
                "cartoon" -> {
                    _recommendationCollabCartoon.value = filteredMovies
                }
                "tv-series" -> {
                    _recommendationCollabTvSeries.value = filteredMovies
                }
            }

        } catch (e: IOException) {
            Log.e("RecommendationVM", "Ошибка сети при получении деталей фильмов", e)
            _error.value = "Ошибка сети. Проверьте подключение к интернету."
        } catch (e: HttpException) {
            Log.e("RecommendationVM", "HTTP ошибка при получении деталей фильмов: ${e.code()}", e)
            _error.value = "Ошибка сервера: ${e.code()}"
        } catch (e: Exception) {
            Log.e("RecommendationVM", "Непредвиденная ошибка при получении деталей фильмов", e)
            _error.value = "Произошла ошибка: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    private fun fetchBooksByIsbn10(isbn10Ids: List<String>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Параллельно запрашиваем книги
                val response = coroutineScope {
                    val deferredList = isbn10Ids.map { isbn ->
                        async {
                            try {
                                RetrofitInstanceBooks.api.searchBookByIsbn("isbn:$isbn")
                            } catch (e: Exception) {
                                Log.e("RecommendationVM", "Ошибка загрузки книги по ISBN $isbn", e)
                                null
                            }
                        }
                    }
                    deferredList.mapNotNull { it.await() }
                }
                val books = response.flatMap { it.items ?: emptyList() }  // собираем все items со всех BookResponse
                    .map { item ->
                        val isbn10 = item.volumeInfo.industryIdentifiers
                            ?.firstOrNull { it.type == "ISBN_10" }
                            ?.identifier ?: "Неизвестно"

                        Book(
                            id = item.id,
                            title = item.volumeInfo.title,
                            authors = item.volumeInfo.authors ?: listOf("Неизвестный автор"),
                            thumbnail = item.volumeInfo.imageLinks?.thumbnail,
                            publishedDate = item.volumeInfo.publishedDate,
                            description = item.volumeInfo.description,
                            isbn10 = isbn10
                        )
                    }

                _recommendationBooks.value = books  // предположим у тебя есть такое поле
                _isLoading.value = false

                Log.d("RecommendationBooks", "Загружены книги: $books")

            } catch (e: Exception) {
                Log.e("RecommendationVM", "Ошибка при получении книг по ISBN", e)
                _error.value = "Ошибка загрузки книг: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun getSameBooksFromGoogleApi(isbn10: String, author: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val response = RetrofitInstanceBooks.api.searchBooks(
                    query = "inauthor:$author",
                    maxResults = 20,  // побольше результатов, вдруг автор плодовитый
                    lang = "ru",
                    orderBy = "relevance"
                )

                val books = response.items.orEmpty()  // безопасно обрабатываем null
                    .map { item ->
                        val isbn = item.volumeInfo.industryIdentifiers
                            ?.firstOrNull { it.type == "ISBN_10" }
                            ?.identifier ?: "Неизвестно"

                        Book(
                            id = item.id,
                            title = item.volumeInfo.title,
                            authors = item.volumeInfo.authors ?: listOf("Неизвестный автор"),
                            thumbnail = item.volumeInfo.imageLinks?.thumbnail,
                            publishedDate = item.volumeInfo.publishedDate,
                            description = item.volumeInfo.description,
                            isbn10 = isbn
                        )
                    }
                    .filter { it.isbn10 != isbn10 } // <--- Вот здесь исключаем саму книгу по ISBN!

                _recommendationBooks.value = books
                _isLoading.value = false

                Log.d("SameBooksFromGoogleApi", "Найдено похожих книг: $books")

            } catch (e: Exception) {
                Log.e("SameBooksFromGoogleApi", "Ошибка поиска похожих книг", e)
                _error.value = "Ошибка загрузки похожих книг: ${e.message}"
                _isLoading.value = false
            }
        }
    }



}