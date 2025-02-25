package com.example.films_shop.main_screen.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    fun fetchAndSaveMovies(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPopularMovies(apiKey)
                FirestoreHelper.saveMoviesToFirestore(response.docs)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
