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
            .collection("favorites_movies")
            .document(favorite.key)
            .set(favorite)
    } else {
        Log.d("MyLog", "Removing movie from favorites: ${favorite.key}")
        db.collection("users")
            .document(uid)
            .collection("favorites_movies")
            .document(favorite.key)
            .delete()
    }

}

fun onBookmarkMovies(
    db: FirebaseFirestore,
    uid: String,
    movie: Movie,
    isBookmark: Boolean
) {
    val bookmark = BookmarkMovie(movie)

    if (isBookmark) {
        Log.d("MyLog", "Adding movie to bookmarks: ${bookmark.key}")
        db.collection("users")
            .document(uid)
            .collection("bookmark_movies")
            .document(bookmark.key)
            .set(bookmark)
    } else {
        Log.d("MyLog", "Removing movie from bookmarks: ${bookmark.key}")
        db.collection("users")
            .document(uid)
            .collection("bookmark_movies")
            .document(bookmark.key)
            .delete()
    }

}

fun onRatedMovies(
    db: FirebaseFirestore,
    uid: String,
    movie: Movie
) {
    val ratedMovie = RatedMovie(movie)

    Log.d("MyLog", "Adding/updating movie rating: ${ratedMovie.key} - ${ratedMovie.userRating}")

    db.collection("users")
        .document(uid)
        .collection("rated_movies")
        .document(ratedMovie.key)
        .set(ratedMovie)
}

