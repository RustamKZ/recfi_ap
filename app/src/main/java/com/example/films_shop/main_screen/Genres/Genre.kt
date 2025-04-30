package com.example.films_shop.main_screen.Genres

import com.google.firebase.firestore.FirebaseFirestore

data class GenreKP(val name: String)

val genres = listOf(
    GenreKP("аниме"),
    GenreKP("биография"),
    GenreKP("боевик"),
    GenreKP("вестерн"),
    GenreKP("военный"),
    GenreKP("детектив"),
    GenreKP("детский"),
    GenreKP("для взрослых"),
    GenreKP("документальный"),
    GenreKP("драма"),
    GenreKP("игра"),
    GenreKP("история"),
    GenreKP("комедия"),
    GenreKP("концерт"),
    GenreKP("короткометражка"),
    GenreKP("криминал"),
    GenreKP("мелодрама"),
    GenreKP("музыка"),
    GenreKP("мультфильм"),
    GenreKP("мюзикл"),
    GenreKP("новости"),
    GenreKP("приключения"),
    GenreKP("реальное ТВ"),
    GenreKP("семейный"),
    GenreKP("спорт"),
    GenreKP("ток-шоу"),
    GenreKP("триллер"),
    GenreKP("ужасы"),
    GenreKP("фантастика"),
    GenreKP("фильм-нуар"),
    GenreKP("фэнтези"),
    GenreKP("церемония")
)

fun saveSelectedGenres(
    db: FirebaseFirestore,
    uid: String,
    selectedGenres: List<GenreKP>
) {
    val genreCollection = db.collection("users")
        .document(uid)
        .collection("favorite_user_genres")

    genreCollection.get().addOnSuccessListener { snapshot ->
        val batch = db.batch()
        for (doc in snapshot.documents) {
            batch.delete(doc.reference)
        }
        for (genre in selectedGenres) {
            val doc = genreCollection.document()
            batch.set(doc, mapOf("name" to genre.name))
        }
        batch.commit()
    }
}

fun loadUserGenres(
    db: FirebaseFirestore,
    uid: String,
    onResult: (List<GenreKP>) -> Unit
) {
    db.collection("users")
        .document(uid)
        .collection("favorite_user_genres")
        .get()
        .addOnSuccessListener { snapshot ->
            val genres = snapshot.documents.mapNotNull {
                it.getString("name")
            }.map { GenreKP(it) }
            onResult(genres)
        }
        .addOnFailureListener {
            onResult(emptyList())
        }
}

