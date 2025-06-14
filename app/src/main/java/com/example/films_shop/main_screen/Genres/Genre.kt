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
    AuthorsGoogle("Ремарк", R.drawable.remark),
    AuthorsGoogle("Достоевский", R.drawable.dost),
    AuthorsGoogle("Лондон", R.drawable.london),
    AuthorsGoogle("Гоголь", R.drawable.gogol),
    AuthorsGoogle("Хемингуэй", R.drawable.heming),
    AuthorsGoogle("Глуховский", R.drawable.gluh),
    AuthorsGoogle("Роулинг", R.drawable.rowling),
    AuthorsGoogle("Лавкрафт", R.drawable.lavcr),
    AuthorsGoogle("Пелевин", R.drawable.pelevin),
    AuthorsGoogle("Оруэлл", R.drawable.oruell),
    AuthorsGoogle("Диккенс", R.drawable.dickenz),
)

//val isbn10List: List<String> = listOf("0002258811", "0002740230", "0002259656",
//    "0002259834", "0003277585", "0002258560", "000433549X", "0002259893",
//    "0002257602", "0002258579", "0385472579"
//)

val isbn10List: List<String> = listOf("0439023483","0439358078","0316015849",
    "0375831002","0451526341","0062024035","0545010225","043965548X","0553588486",
    "0446675539","1400096898","0439064864","0786838655","0316769177","0060256656","0142437204",
    "0345538374","0618260307","0062315005","0140283331", "0143058142", "0451527747", "0385333846",
    "059309932X", "055357342X", "0450040186", "0142437174"
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

