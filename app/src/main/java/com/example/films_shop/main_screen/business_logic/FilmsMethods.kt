package com.example.films_shop.main_screen.business_logic

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.films_shop.main_screen.api.Genre
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.Poster
import com.example.films_shop.main_screen.api.Rating
import com.example.films_shop.main_screen.data.Favorite
import com.example.films_shop.main_screen.data.FavoriteMovie
import com.example.films_shop.main_screen.data.Film
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

fun getFavoriteMovies(
    db: FirebaseFirestore,
    uid: String,
    onResult: (List<Movie>) -> Unit
) {
    db.collection("users")
        .document(uid)
        .collection("favorites")
        .get()
        .addOnSuccessListener { snapshot ->
            val movies = snapshot.documents.mapNotNull { it.toObject(FavoriteMovie::class.java) }
                .map { favorite ->
                    Movie(
                        id = favorite.key,
                        name = favorite.name,
                        year = favorite.year,
                        poster = favorite.posterUrl?.let { Poster(it) },
                        genres = favorite.genres?.map { Genre(it) },
                        rating = Rating(favorite.rating ?: 0.0),
                        isFavorite = true
                    )
                }
            onResult(movies)
        }
}

fun getAllFavFilms(
    db: FirebaseFirestore,
    idsList: List<String>,
    onFilms: (List<Film>) -> Unit
) {
    if (idsList.isNotEmpty()) {
        db.collection("films")
            .whereIn(FieldPath.documentId(), idsList)
            .get()
            .addOnSuccessListener { task ->
                val filmsList = task.toObjects(Film::class.java).map {
                    if (idsList.contains(it.key)) {
                        it.copy(isFavorite = true)
                    } else {
                        it
                    }
                }
                onFilms(filmsList)
            }
            .addOnFailureListener {

            }
    } else {
        onFilms(emptyList())
    }

}

fun getAllFavsIds(
    db: FirebaseFirestore,
    uid: String,
    onFavs: (List<String>) -> Unit
) {
    db.collection("users")
        .document(uid)
        .collection("favorites")
        .get()
        .addOnSuccessListener { task ->
            val idsList = task.toObjects(Favorite::class.java)
            val keysList = arrayListOf<String>()
            idsList.forEach {
                keysList.add(it.key)
            }
            onFavs(keysList)
        }
}

fun getAllFilms(
    db: FirebaseFirestore,
    idsList: List<String>,
    genre: String = "",
    flagFilter: Boolean = false,
    onFilms: (List<Film>) -> Unit
) {
    if (flagFilter) {
        db.collection("films")
            .whereEqualTo("genre", genre)
            .get()
            .addOnSuccessListener { task ->
                val filmsList = task.toObjects(Film::class.java).map {
                    if (idsList.contains(it.key)) {
                        it.copy(isFavorite = true)
                    } else {
                        it
                    }
                }
                onFilms(filmsList)
            }
            .addOnFailureListener {

            }
    }
    else {
        db.collection("films")
            .get()
            .addOnSuccessListener { task ->
                val filmsList = task.toObjects(Film::class.java).map {
                    if (idsList.contains(it.key)) {
                        it.copy(isFavorite = true)
                    } else {
                        it
                    }
                }
                onFilms(filmsList)
            }
            .addOnFailureListener {

            }
    }
}

fun onFavs(
    db: FirebaseFirestore,
    uid: String,
    favorite: Favorite,
    isFavorite: Boolean
) {
    if (isFavorite) {
        db.collection("users")
            .document(uid)
            .collection("favorites")
            .document(favorite.key)
            .set(favorite)
    } else {
        db.collection("users")
            .document(uid)
            .collection("favorites")
            .document(favorite.key)
            .delete()
    }
}

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



fun ImageToBase64(
    uri: Uri,
    contentResolver: ContentResolver
): String {
    val inputStream = contentResolver.openInputStream(uri)
    val bytes = inputStream?.readBytes()
    return bytes?.let {
        Base64.encodeToString(it, Base64.DEFAULT)
    } ?: ""
}

fun saveFilmToFireStore(
    fireStore: FirebaseFirestore,
    film: Film,
    onSaved: () -> Unit,
    onError: () -> Unit
) {
    val db = fireStore.collection("films")
    val key = film.key.ifEmpty() { db.document().id }
    db.document(key)
        .set(
            film.copy(
                key = key
            )
        )
        .addOnSuccessListener {
            onSaved()
        }
        .addOnFailureListener {
            onError()
        }
}

fun compressAndConvertToBase64(uri: Uri, contentResolver: ContentResolver): String {
    val inputStream = contentResolver.openInputStream(uri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)

    val outputStream = ByteArrayOutputStream()
    originalBitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream) // Сжатие до 50% качества

    val compressedBytes = outputStream.toByteArray()
    return Base64.encodeToString(compressedBytes, Base64.DEFAULT)
}