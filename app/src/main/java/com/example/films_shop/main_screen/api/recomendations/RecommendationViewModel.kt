package com.example.films_shop.main_screen.api.recomendations

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RecommendationViewModel : ViewModel() {

    private val _recommendations = mutableStateOf<List<String>>(emptyList())
    val recommendations: State<List<String>> = _recommendations

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchRecommendations(filmId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.getRecommendations(filmId)
                _recommendations.value = response.map { it.id.toString() }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            }
        }
    }
}

