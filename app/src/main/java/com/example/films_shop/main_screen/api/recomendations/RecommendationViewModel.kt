package com.example.films_shop.main_screen.api.recomendations

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.Poster
import com.example.films_shop.main_screen.api.RetrofitInstance
import com.example.films_shop.main_screen.api.apiKey
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RecommendationViewModel : ViewModel() {

    private val _tmdbRecommendationIds = mutableStateOf<List<Int>>(emptyList())
    val tmdbRecommendationIds: State<List<Int>> = _tmdbRecommendationIds

    private val _recommendationMovies = mutableStateOf<List<Movie>>(emptyList())
    val recommendationMovies: State<List<Movie>> = _recommendationMovies

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
                    poster = movie.poster?.copy(url = movie.poster.url ?: "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg")
                        ?: Poster("https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg"),
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
}