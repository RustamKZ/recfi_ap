package com.example.films_shop.main_screen.business_logic

import android.util.Log
import com.example.films_shop.main_screen.api.BookApi.Book
import com.example.films_shop.main_screen.business_logic.data_classes.BookmarkBook
import com.example.films_shop.main_screen.business_logic.data_classes.FavoriteBook
import com.example.films_shop.main_screen.business_logic.data_classes.RatedBook
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

fun onBookmarkBooks(
    db: FirebaseFirestore,
    uid: String,
    book: Book,
    isBookmark: Boolean
) {
    val bookmark = BookmarkBook(book)

    if (isBookmark) {
        Log.d("MyLog", "Adding book to BookmarkBooks: ${bookmark.key}")
        db.collection("users")
            .document(uid)
            .collection("bookmark_books")
            .document(bookmark.key)
            .set(bookmark)
    } else {
        Log.d("MyLog", "Removing book from BookmarkBooks: ${bookmark.key}")
        db.collection("users")
            .document(uid)
            .collection("bookmark_books")
            .document(bookmark.key)
            .delete()
    }
}

fun onRatedBooks(
    db: FirebaseFirestore,
    uid: String,
    book: Book
) {
    val ratedBook = RatedBook(book)

    Log.d("MyLog", "Adding/updating movie rating: ${ratedBook.key} - ${ratedBook.userRating}")

    db.collection("users")
        .document(uid)
        .collection("rated_books")
        .document(ratedBook.key)
        .set(ratedBook)
}