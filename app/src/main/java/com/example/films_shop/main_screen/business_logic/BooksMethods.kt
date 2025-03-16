package com.example.films_shop.main_screen.business_logic

import android.util.Log
import com.example.films_shop.main_screen.api.BookApi.Book
import com.example.films_shop.main_screen.api.Movie
import com.google.firebase.firestore.FirebaseFirestore

fun onFavsBooks(
    db: FirebaseFirestore,
    uid: String,
    book: Book,
    isFavorite: Boolean
) {
    val favorite = FavoriteBook(book)

    if (isFavorite) {
        Log.d("MyLog", "Adding book to favorites: ${favorite.key}")
        db.collection("users")
            .document(uid)
            .collection("favorites_books")
            .document(favorite.key)
            .set(favorite)
    } else {
        Log.d("MyLog", "Removing book from favorites: ${favorite.key}")
        db.collection("users")
            .document(uid)
            .collection("favorites_books")
            .document(favorite.key)
            .delete()
    }
}