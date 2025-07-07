package com.example.films_shop.main_screen.api.recomendations

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.films_shop.main_screen.Genres.AuthorsGoogle
import com.example.films_shop.main_screen.Genres.GenreKP
import com.example.films_shop.main_screen.api.Backdrop
import com.example.films_shop.main_screen.api.BookApi.Book
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.api.BookApi.RetrofitInstanceBooks
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.MovieResponse
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

    private val _tmdbCollabRecommendationIdsMovies = mutableStateOf<List<Int>>(emptyList())
    private val _recommendationCollabMovies = mutableStateOf<List<Movie>>(emptyList())
    val recommendationCollabMovies: State<List<Movie>> = _recommendationCollabMovies

    private val _tmdbCollabRecommendationIdsCartoon = mutableStateOf<List<Int>>(emptyList())
    private val _recommendationCollabCartoon = mutableStateOf<List<Movie>>(emptyList())
    val recommendationCollabCartoon: State<List<Movie>> = _recommendationCollabCartoon

    private val _tmdbCollabRecommendationIdsTvSeries = mutableStateOf<List<Int>>(emptyList())
    private val _recommendationCollabTvSeries = mutableStateOf<List<Movie>>(emptyList())
    val recommendationCollabTvSeries: State<List<Movie>> = _recommendationCollabTvSeries

    private val _isbn10CollabRecommendationIds = mutableStateOf<List<String>>(emptyList())
    private val _recommendationCollabBooks = mutableStateOf<List<Book>>(emptyList())
    val recommendationCollabBooks: State<List<Book>> = _recommendationCollabBooks
    //Коллаборативная фильтрация

    //Жанры и авторы
    private val _recommendationMoviesGenre = mutableStateOf<List<Movie>>(emptyList())
    val recommendationMoviesGenre: State<List<Movie>> = _recommendationMoviesGenre

    private val _recommendationCartoonGenre = mutableStateOf<List<Movie>>(emptyList())
    val recommendationCartoonGenre: State<List<Movie>> = _recommendationCartoonGenre

    private val _recommendationTvSeriesGenre = mutableStateOf<List<Movie>>(emptyList())
    val recommendationTvSeriesGenre: State<List<Movie>> = _recommendationTvSeriesGenre

    private val _recommendationBooksAuthor = mutableStateOf<List<Book>>(emptyList())
    val recommendationBooksAuthor: State<List<Book>> = _recommendationBooksAuthor
    //Жанры и авторы


    private val _isbn10RecommendationIds = mutableStateOf<List<String>>(emptyList())

    private val _recommendationBooks = mutableStateOf<List<Book>>(emptyList())
    val recommendationBooks: State<List<Book>> = _recommendationBooks

    private val _booksDataset = mutableStateOf<List<Book>>(emptyList())
    val booksDataset: State<List<Book>> = _booksDataset

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
                fetchMoviesByTmdbIds(tmdbIds, type)

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
        nRecommendations: Int = 20
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

                Log.d("RecommendationVM", "Коллаб рекомендованные тип $type ID: $tmdbIds")
                // Проверяем, есть ли рекомендации
                if (tmdbIds.isEmpty()) {
                    when (type) {
                        "movie" -> {
                            _recommendationCollabMovies.value = emptyList()
                            _isLoading.value = false
                            return@launch
                        }
                        "cartoon" -> {
                            _recommendationCollabCartoon.value = emptyList()
                            _isLoading.value = false
                            return@launch
                        }
                        "tv-series" -> {
                            _recommendationCollabTvSeries.value = emptyList()
                            _isLoading.value = false
                            return@launch
                        }
                    }
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

    fun getGenreFilms(selectedGenres: List<GenreKP>) {
        viewModelScope.launch {
            val genreNames = selectedGenres.map { it.name.lowercase() }

            val movies = try {
                val response = RetrofitInstance.api.getMoviesByGenres(
                    genres = genreNames,
                    type = "movie",
                    apiKey = apiKey,
                )
                processMovies(response)
            } catch (e: Exception) {
                Log.e("GenreAuthor", "Ошибка при загрузке фильмов: ${e.message}")
                emptyList()
            }

            val cartoons = try {
                val response = RetrofitInstance.api.getMoviesByGenres(
                    genres = genreNames,
                    type = "cartoon",
                    apiKey = apiKey,
                )
                processMovies(response)
            } catch (e: Exception) {
                Log.e("GenreAuthor", "Ошибка при загрузке мультфильмов: ${e.message}")
                emptyList()
            }

            val series = try {
                val response = RetrofitInstance.api.getMoviesByGenres(
                    genres = genreNames,
                    type = "tv-series",
                    apiKey = apiKey,
                )
                processMovies(response)
            } catch (e: Exception) {
                Log.e("GenreAuthor", "Ошибка при загрузке сериалов: ${e.message}")
                emptyList()
            }

            // Просто присваиваем — если произошла ошибка, список будет пустым
            _recommendationMoviesGenre.value = movies
            _recommendationCartoonGenre.value = cartoons
            _recommendationTvSeriesGenre.value = series

            Log.d("GenreAuthor", "Movies: ${movies.size}")
            Log.d("GenreAuthor", "Cartoon: ${cartoons.size}")
            Log.d("GenreAuthor", "TvSeries: ${series.size}")
        }
    }


    fun getAuthorBooks(selectedAuthors: List<AuthorsGoogle>) {
        viewModelScope.launch {
            try {
                val allBooks = mutableListOf<Book>()

                selectedAuthors.forEach { author ->
                    try {
                        val response = RetrofitInstanceBooks.api.searchBooks(
                            query = "inauthor:${author.name}",
                            maxResults = 30,
                            lang = "ru",
                            orderBy = "relevance"
                        )
                        val books = response.items.orEmpty()
                            .map { item ->
                                val isbn = item.volumeInfo.industryIdentifiers
                                    ?.firstOrNull { it.type == "ISBN_10" }
                                    ?.identifier ?: "Неизвестно"
                                val thumbnail = item.volumeInfo.imageLinks?.run {
                                    extraLarge ?: large ?: medium ?: small ?: thumbnail ?: smallThumbnail
                                } ?: ""

                                Book(
                                    id = item.id,
                                    title = item.volumeInfo.title,
                                    authors = item.volumeInfo.authors ?: listOf("Неизвестный автор"),
                                    thumbnail = thumbnail,
                                    publishedDate = item.volumeInfo.publishedDate,
                                    description = item.volumeInfo.description,
                                    isbn10 = isbn,
                                    publisher = item.volumeInfo.publisher,
                                    pageCount = item.volumeInfo.pageCount,
                                    categories = item.volumeInfo.categories,
                                    averageRating = item.volumeInfo.averageRating,
                                    ratingsCount = item.volumeInfo.ratingsCount,
                                    language = item.volumeInfo.language
                                )
                            }
                            .filter { book ->
                                book.title.isNotBlank() &&
                                        book.authors != listOf("Неизвестный автор") &&
                                        !book.thumbnail.isNullOrBlank() &&
                                        !book.description.isNullOrBlank() &&
                                        book.description != "Описание отсутствует"
                            }
                            .sortedWith(
                                compareByDescending<Book> { it.averageRating ?: 0.0 }
                                    .thenByDescending { it.ratingsCount ?: 0 }
                            )
                            .take(10)

                        allBooks.addAll(books)
                    } catch (e: Exception) {
                        Log.e("AuthorBooks", "Ошибка при загрузке книг автора: ${author.name}", e)
                    }
                }

                val uniqueBooks = allBooks.distinctBy { it.id }

                _recommendationBooksAuthor.value = uniqueBooks

                Log.d("AuthorBooks", "Книг по авторам загружено: ${uniqueBooks.size}")

            } catch (e: Exception) {
                Log.e("RecommendationVM", "Ошибка при загрузке книг по авторам: ${e.message}")
            }
        }
    }

    // Обработка фильмов
    private fun processMovies(response: MovieResponse): List<Movie> {
        val defaultPosterUrl = "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg"

        val updatedMovies = response.docs.map { movie ->
            movie.copy(
                poster = movie.poster?.url?.let { Poster(it) } ?: Poster(defaultPosterUrl),
                backdrop = when {
                    movie.backdrop?.url != null -> Backdrop(movie.backdrop.url)
                    movie.poster?.url != null -> Backdrop(movie.poster.url)
                    else -> Backdrop(defaultPosterUrl)
                },
                persons = movie.persons?.filter { it.profession == "режиссеры" } ?: emptyList()
            )
        }

        return updatedMovies
            .filter { movie -> movie.name != null && movie.poster?.url != defaultPosterUrl }
            .sortedByDescending { movie ->
                movie.rating?.kp?.takeIf { it > 0.0 }
                    ?: movie.rating?.imdb?.takeIf { it > 0.0 }
                    ?: 0.0
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
                    _recommendationBooks.value = emptyList()
                    _isLoading.value = false
                    getSameBooksFromGoogleApi(isbn10, authors)
                    return@launch
                }

                fetchBooksByIsbn10(isbn10Ids)

            } catch (e: Exception) {
                Log.e("RecommendationVM", "Ошибка при получении рекомендаций", e)
                _error.value = "Ошибка получения рекомендаций: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun fetchCollabRecommendationsBooks(
        ratings: Map<String, Int>,
        bookViewModel: BookViewModel,
        nRecommendations: Int = 20
    ) {
        Log.d("Debug", "Fetching collab recommendations for books, ratings: $ratings")
        if (ratings.isEmpty()) {
            _error.value = "Не передано ни одной оценки книг"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Получаем рекомендации (ID фильмов из TMDB)
                Log.d("RecommendationCollabBooks", "Получены ratings: $ratings")
                val response = ApiClient.api.getCollaborativeRecommendationsBooks(
                    nRecommendations = nRecommendations,
                    ratings = ratings
                )
                if (response.isNotEmpty()) {
                    _isbn10CollabRecommendationIds.value = response
                }
                Log.d("RecommendationVM", "Коллаб рекомендованные isn10 книги: ${_isbn10CollabRecommendationIds.value}")
                if (response.isEmpty()) {
                    getSameBooksFromGoogleApiColab(bookViewModel)
                    _isLoading.value = false
                    return@launch
                }

                // Теперь запрашиваем информацию о книгах
                fetchBooksByIsbn10Collab(response)

            } catch (e: Exception) {
                Log.e("RecommendationVM", "Ошибка при получении коллаб рекомендаций книг", e)
                _error.value = "Ошибка получения коллаб рекомендаций книг: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchMoviesByTmdbIds(tmdbIds: List<Int>, type: String) {
        var filteredMovies: List<Movie> = emptyList()

        try {
            val response = RetrofitInstance.api.getMoviesByTmdbIds(
                apiKey = apiKey,
                tmdbIds = tmdbIds
            )

            Log.d("RecommendationVM", "Получены детали фильмов: ${response.docs.size}")

            val updatedMovies = response.docs.map { movie ->
                movie.copy(
                    poster = if (movie.poster?.url != null) {
                        movie.poster.copy(url = movie.poster.url)
                    } else {
                        Poster("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg")
                    },
                    backdrop = when {
                        movie.backdrop?.url != null -> movie.backdrop.copy(url = movie.backdrop.url)
                        movie.poster?.url != null -> Backdrop(movie.poster.url)
                        else -> Backdrop("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg")
                    },
                    persons = movie.persons?.filter { it.profession == "режиссеры" } ?: emptyList()
                )
            }


            filteredMovies = updatedMovies
                .filter { it.type == type && it.name != null && it.poster?.url != "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg" }
                .sortedByDescending {
                    when {
                        it.rating?.kp != null && it.rating.kp > 0.0 -> it.rating.kp
                        it.rating?.imdb != null && it.rating.imdb > 0.0 -> it.rating.imdb
                        else -> 0.0
                    }
                }

        } catch (e: Exception) {
            Log.e("RecommendationVM", "Ошибка при получении деталей фильмов", e)
            // Игнорируем ошибку, не показываем _error и не крашим
        } finally {
            _recommendationMovies.value = filteredMovies
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
                        movie.backdrop != null && movie.backdrop.url != null ->
                            movie.backdrop.copy(url = movie.backdrop.url)
                        movie.poster != null && movie.poster.url != null ->
                            Backdrop(movie.poster.url)
                        else ->
                            Backdrop("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg")
                    },
                    persons = movie.persons?.filter { it.profession == "режиссеры" } ?: emptyList()
                )
            }

            // Фильтруем фильмы без названия
            val filteredMovies = updatedMovies.filterNot {
                it.name == null || it.poster == Poster("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg")
            }
                .sortedByDescending { movie ->
                    when {
                        movie.rating?.kp != null && movie.rating.kp > 0.0 -> movie.rating.kp
                        movie.rating?.imdb != null && movie.rating.imdb > 0.0 -> movie.rating.imdb
                        else -> 0.0
                    }
                }
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

                val response = coroutineScope {
                    val deferredList = isbn10Ids.map { isbn ->
                        async {
                            try {
                                RetrofitInstanceBooks.api.searchBookByIsbn("isbn:$isbn", "AIzaSyAKHz0gmZ5IWWlvSGcw-ATX-8hMzm5dFJQ")
                            } catch (e: Exception) {
                                Log.e("RecommendationVM", "Ошибка загрузки книги по ISBN $isbn", e)
                                null
                            }
                        }
                    }
                    deferredList.mapNotNull { it.await() }
                }
                val books = response.flatMap { it.items ?: emptyList() }
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
                            isbn10 = isbn10,
                            publisher = item.volumeInfo.publisher,
                            pageCount = item.volumeInfo.pageCount,
                            categories = item.volumeInfo.categories,
                            averageRating = item.volumeInfo.averageRating,
                            ratingsCount = item.volumeInfo.ratingsCount,
                            language = item.volumeInfo.language
                        )
                    }

                _recommendationBooks.value = books
                _isLoading.value = false

                Log.d("RecommendationBooks", "Загружены книги: ${_recommendationBooks.value}")

            } catch (e: Exception) {
                Log.e("RecommendationVM", "Ошибка при получении книг по ISBN", e)
                _error.value = "Ошибка загрузки книг: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun getCustomBooksFromDataset(isbn10Ids: List<String>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val response = coroutineScope {
                    val deferredList = isbn10Ids.map { isbn ->
                        async {
                            try {
                                RetrofitInstanceBooks.api.searchBookByIsbn("isbn:$isbn", "AIzaSyAKHz0gmZ5IWWlvSGcw-ATX-8hMzm5dFJQ")
                            } catch (e: Exception) {
                                Log.e("RecommendationVM", "Ошибка загрузки книги по ISBN $isbn", e)
                                null
                            }
                        }
                    }
                    deferredList.mapNotNull { it.await() }
                }
                val books = response.flatMap { it.items ?: emptyList() }
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
                            isbn10 = isbn10,
                            publisher = item.volumeInfo.publisher,
                            pageCount = item.volumeInfo.pageCount,
                            categories = item.volumeInfo.categories,
                            averageRating = item.volumeInfo.averageRating,
                            ratingsCount = item.volumeInfo.ratingsCount,
                            language = item.volumeInfo.language
                        )
                    }
                    .filter { book ->
                        book.title.isNotBlank() &&
                                book.authors != listOf("Неизвестный автор") &&
                                !book.thumbnail.isNullOrBlank() &&
                                !book.description.isNullOrBlank() &&
                                book.description != "Описание отсутствует"
                    }
                    .sortedWith(
                        compareByDescending<Book> { it.averageRating ?: 0.0 }
                            .thenByDescending { it.ratingsCount ?: 0 }
                    )

                _booksDataset.value = books
                _isLoading.value = false

                Log.d("RecommendationCustomBooks", "Загружены книги: ${_booksDataset.value}")

            } catch (e: Exception) {
                Log.e("RecommendationVM", "Ошибка при получении книг по ISBN", e)
                _error.value = "Ошибка загрузки книг: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun fetchBooksByIsbn10Collab(isbn10Ids: List<String>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val response = coroutineScope {
                    val deferredList = isbn10Ids.map { isbn ->
                        async {
                            try {
                                RetrofitInstanceBooks.api.searchBookByIsbn("isbn:$isbn", "AIzaSyAKHz0gmZ5IWWlvSGcw-ATX-8hMzm5dFJQ")
                            } catch (e: Exception) {
                                Log.e("RecommendationVM", "Ошибка загрузки книги по ISBN $isbn", e)
                                null
                            }
                        }
                    }
                    deferredList.mapNotNull { it.await() }
                }
                val books = response.flatMap { it.items ?: emptyList() }
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
                            isbn10 = isbn10,
                            publisher = item.volumeInfo.publisher,
                            pageCount = item.volumeInfo.pageCount,
                            categories = item.volumeInfo.categories,
                            averageRating = item.volumeInfo.averageRating,
                            ratingsCount = item.volumeInfo.ratingsCount,
                            language = item.volumeInfo.language
                        )
                    }

                _recommendationCollabBooks.value = books
                _isLoading.value = false

                Log.d("RecommendationBooks", "Загружены коллаб рекомендованные книги: ${_recommendationCollabBooks.value}")

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
                    maxResults = 20,
                    lang = "ru",
                    orderBy = "relevance"
                )

                val books = response.items.orEmpty()
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
                            isbn10 = isbn,
                            publisher = item.volumeInfo.publisher,
                            pageCount = item.volumeInfo.pageCount,
                            categories = item.volumeInfo.categories,
                            averageRating = item.volumeInfo.averageRating,
                            ratingsCount = item.volumeInfo.ratingsCount,
                            language = item.volumeInfo.language
                        )
                    }
                    .filter { it.isbn10 != isbn10 }

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

    private fun getSameBooksFromGoogleApiColab(bookViewModel: BookViewModel) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val ratedBooks = bookViewModel.ratedBooksTripleState.value

                // Фильтруем только те книги, у которых есть автор и рейтинг > 2
                val filteredRatedBooks = ratedBooks
                    .filter { it.author.isNotBlank() && it.userRating > 2.0 }

                val existingIsbn10s = ratedBooks.map { it.isbn10 }.toSet()
                val collectedBooks = mutableListOf<Book>()
                val processedAuthors = mutableSetOf<String>()

                for (rated in filteredRatedBooks.sortedByDescending { it.userRating }) {
                    val author = rated.author
                    if (author in processedAuthors) continue
                    processedAuthors.add(author)

                    // Определяем, сколько книг брать в зависимости от userRating
                    val booksToFetch = when {
                        rated.userRating >= 5 -> 8
                        rated.userRating >= 4 -> 6
                        rated.userRating >= 3 -> 4
                        else -> 2
                    }

                    val response = RetrofitInstanceBooks.api.searchBooks(
                        query = "inauthor:$author",
                        maxResults = booksToFetch,
                        lang = "ru",
                        orderBy = "relevance"
                    )

                    val books = response.items.orEmpty()
                        .mapNotNull { item ->
                            val isbn = item.volumeInfo.industryIdentifiers
                                ?.firstOrNull { it.type == "ISBN_10" }
                                ?.identifier ?: return@mapNotNull null

                            if (isbn in existingIsbn10s) return@mapNotNull null

                            Book(
                                id = item.id,
                                title = item.volumeInfo.title,
                                authors = item.volumeInfo.authors ?: listOf("Неизвестный автор"),
                                thumbnail = item.volumeInfo.imageLinks?.thumbnail,
                                publishedDate = item.volumeInfo.publishedDate,
                                description = item.volumeInfo.description,
                                isbn10 = isbn,
                                publisher = item.volumeInfo.publisher,
                                pageCount = item.volumeInfo.pageCount,
                                categories = item.volumeInfo.categories,
                                averageRating = item.volumeInfo.averageRating,
                                ratingsCount = item.volumeInfo.ratingsCount,
                                language = item.volumeInfo.language
                            )
                        }

                    collectedBooks.addAll(books)

                    if (collectedBooks.size >= 30) break
                }

                _recommendationCollabBooks.value = collectedBooks.take(30)
                _isLoading.value = false

                Log.d("CollabRecommendations", "Найдено рекомендованных книг: ${collectedBooks.size}")

            } catch (e: Exception) {
                Log.e("CollabRecommendations", "Ошибка загрузки рекомендаций", e)
                _error.value = "Ошибка загрузки рекомендаций: ${e.message}"
                _isLoading.value = false
            }
        }
    }

}