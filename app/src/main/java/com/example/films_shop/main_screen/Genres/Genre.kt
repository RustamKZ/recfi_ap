package com.example.films_shop.main_screen.Genres

import androidx.annotation.DrawableRes
import com.example.films_shop.R
import com.google.firebase.firestore.FirebaseFirestore

data class GenreKP(val name: String, @DrawableRes val imageResId: Int)

val genres = listOf(
    GenreKP("аниме", R.drawable.anime),
    GenreKP("биография", R.drawable.authobiag),
    GenreKP("комедия", R.drawable.comedy),
    GenreKP("боевик", R.drawable.boevik),
    GenreKP("ужасы", R.drawable.scary),
    GenreKP("драма", R.drawable.drama),
    GenreKP("спорт", R.drawable.sport),
    GenreKP("криминал", R.drawable.criminal),
    GenreKP("детектив", R.drawable.detectiv),
    GenreKP("триллер", R.drawable.triller),
    GenreKP("фантастика", R.drawable.fantastika),
    GenreKP("мультфильм", R.drawable.cartoon),
    GenreKP("мюзикл", R.drawable.musicl),
    GenreKP("музыка", R.drawable.music),
    GenreKP("короткометражка", R.drawable.corotko),
    GenreKP("детский", R.drawable.detsckii),
    GenreKP("военный", R.drawable.voenii),
    GenreKP("документальный", R.drawable.document),
    GenreKP("семейный", R.drawable.family)
)

data class AuthorsGoogle(val name: String, @DrawableRes val imageResId: Int)

val authors = listOf(
    AuthorsGoogle("Э.М.Ремарк", R.drawable.remark),
    AuthorsGoogle("Ф.Достоевский", R.drawable.dost),
    AuthorsGoogle("Дж.Лондон", R.drawable.london),
    AuthorsGoogle("Н.Гоголь", R.drawable.gogol),
    AuthorsGoogle("Э.Хэмингуэй", R.drawable.heming),
    AuthorsGoogle("Р.Брэдбери", R.drawable.bredbjpg),
    AuthorsGoogle("Д.Глуховский", R.drawable.gluh),
    AuthorsGoogle("Дж.К.Роулинг", R.drawable.rowling),
    AuthorsGoogle("Дж.Р.Р.Мартин", R.drawable.martin),
    AuthorsGoogle("Г.Лавкрафт", R.drawable.lavcr),
    AuthorsGoogle("В.Пелевин", R.drawable.pelevin),
    AuthorsGoogle("Дж.Оруэлл", R.drawable.oruell),
    AuthorsGoogle("О.Уальд", R.drawable.oscar),
    AuthorsGoogle("Ч.Диккенс", R.drawable.dickenz),
)

fun saveSelectedGenres(
    db: FirebaseFirestore,
    uid: String,
    selectedGenres: List<GenreKP>,
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
            }.map { GenreKP(it, R.drawable.test_poster) }
            onResult(genres)
        }
        .addOnFailureListener {
            onResult(emptyList())
        }
}

fun saveSelectedAuthors(
    db: FirebaseFirestore,
    uid: String,
    selectedAuthors: List<AuthorsGoogle>,
) {
    val genreCollection = db.collection("users")
        .document(uid)
        .collection("favorite_user_book_authors")

    genreCollection.get().addOnSuccessListener { snapshot ->
        val batch = db.batch()
        for (doc in snapshot.documents) {
            batch.delete(doc.reference)
        }
        for (author in selectedAuthors) {
            val doc = genreCollection.document()
            batch.set(doc, mapOf("name" to author.name))
        }
        batch.commit()
    }
}

fun loadUserAuthors(
    db: FirebaseFirestore,
    uid: String,
    onResult: (List<AuthorsGoogle>) -> Unit
) {
    db.collection("users")
        .document(uid)
        .collection("favorite_user_book_authors")
        .get()
        .addOnSuccessListener { snapshot ->
            val authors = snapshot.documents.mapNotNull {
                it.getString("name")
            }.map { AuthorsGoogle(it, R.drawable.test_poster) }
            onResult(authors)
        }
        .addOnFailureListener {
            onResult(emptyList())
        }
}

