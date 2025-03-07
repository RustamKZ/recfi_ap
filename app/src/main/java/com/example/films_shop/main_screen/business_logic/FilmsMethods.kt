package com.example.films_shop.main_screen.business_logic

import android.util.Log
import com.example.films_shop.main_screen.api.Movie
import com.google.firebase.firestore.FirebaseFirestore

fun onFavsMovies(
    db: FirebaseFirestore,
    uid: String,
    movie: Movie,
    isFavorite: Boolean
) {
    val favorite = FavoriteMovie(movie)

    if (isFavorite) {
        Log.d("MyLog", "Adding movie to favorites: ${favorite.key}")
        db.collection("users")
            .document(uid)
            .collection("favorites")
            .document(favorite.key)
            .set(favorite)
    } else {
        Log.d("MyLog", "Removing movie from favorites: ${favorite.key}")
        db.collection("users")
            .document(uid)
            .collection("favorites")
            .document(favorite.key)
            .delete()
    }
}
