package com.example.films_shop.main_screen.api

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()

    fun saveMoviesToFirestore(movies: List<Movie>) {
        movies.forEach { movie ->
            val movieData = hashMapOf(
                "id" to movie.id,
                "title" to movie.name,
                "overview" to (movie.description ?: "Нет описания"),
                "release_year" to (movie.year ?: "Неизвестно"),
                "poster_url" to (movie.poster?.url ?: ""),
                "rating" to (movie.rating?.kp ?: 0.0)
            )

            db.collection("movies").document(movie.id.toString())
                .set(movieData)
                .addOnSuccessListener { Log.d("Firestore", "Фильм ${movie.name} загружен!") }
                .addOnFailureListener { e -> Log.w("Firestore", "Ошибка: ", e) }
        }
    }
}
