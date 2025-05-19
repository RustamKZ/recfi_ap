package com.example.films_shop.main_screen.api.BookApi

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.films_shop.main_screen.business_logic.data_classes.BookmarkBook
import com.example.films_shop.main_screen.business_logic.data_classes.FavoriteBook
import com.example.films_shop.main_screen.business_logic.data_classes.RatedBook
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest

data class RatedBookTriple(
    val isbn10: String,
    val author: String,
    val userRating: Int
)


class BookViewModel : ViewModel() {
    private val _query = MutableStateFlow("роман") // По умолчанию ищем "python"
    val authors = listOf("Ремарк", "Булгаков", "Достоевский")
    val favoriteBooksState = mutableStateOf<List<Book>>(emptyList())
    val bookmarkBooksState = mutableStateOf<List<Book>>(emptyList())
    val ratedBooksState = mutableStateOf<List<Book>>(emptyList())
    val ratedBooksMapState = MutableStateFlow<Map<String, Int>>(emptyMap())
    val ratedBooksTripleState = MutableStateFlow<List<RatedBookTriple>>(emptyList())
    @OptIn(ExperimentalCoroutinesApi::class)
    val bookPagingFlow: Flow<PagingData<Book>> = _query
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = 10, prefetchDistance = 2),
                pagingSourceFactory = { BookPagingSource(RetrofitInstanceBooks.api,authors ) }
            ).flow.cachedIn(viewModelScope)
        }
    fun isInFavorites(id: String): Boolean {
        return favoriteBooksState.value.any { it.id == id }
    }

    fun isInBookmarks(id: String): Boolean {
        return bookmarkBooksState.value.any { it.id == id }
    }
    fun getBooksByAuthors(authors: List<String>): Flow<PagingData<Book>> {
        return Pager(PagingConfig(pageSize = 30)) {
            BookPagingSource(RetrofitInstanceBooks.api, authors)
        }.flow.cachedIn(viewModelScope)
    }

    fun isInRated(id: String): Boolean {
        return ratedBooksState.value.any { it.id == id }
    }
    fun loadFavoriteBooks(db: FirebaseFirestore, uid: String) {
        db.collection("users").document(uid).collection("favorites_books")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog", "Ошибка загрузки избранных книг", error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val books = it.documents.mapNotNull { doc -> doc.toObject(FavoriteBook::class.java) }
                        .map { favorite ->
                            Book(
                                id = favorite.key,
                                isbn10 = favorite.isbn10,
                                title = favorite.title?: "",
                                authors = favorite.authors,
                                thumbnail = favorite.thumbnail,
                                publishedDate = favorite.publishedDate?: "Неизвестно",
                                description = favorite.description,
                                isFavorite = true,
                                isBookMark = favorite.isBookMark,
                                isRated = favorite.isRated,
                                userRating = favorite.userRating
                            )
                        }
                    favoriteBooksState.value = books
                    Log.d("MyLog", "Избранное обновлено, всего: ${books.size}")
                }
            }
    }
    fun loadBookmarkBooks(db: FirebaseFirestore, uid: String) {
        db.collection("users").document(uid).collection("bookmark_books")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog", "Ошибка загрузки запланированных книг", error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val books = it.documents.mapNotNull { doc -> doc.toObject(BookmarkBook::class.java) }
                        .map { bookmark ->
                            Book(
                                id = bookmark.key,
                                isbn10 = bookmark.isbn10,
                                title = bookmark.title?: "",
                                authors = bookmark.authors,
                                thumbnail = bookmark.thumbnail,
                                publishedDate = bookmark.publishedDate?: "Неизвестно",
                                description = bookmark.description,
                                isBookMark = true,
                                isFavorite = bookmark.isFavorite,
                                isRated = bookmark.isRated,
                                userRating = bookmark.userRating
                            )
                        }
                    bookmarkBooksState.value = books
                    Log.d("MyLog", "Прочитать позже обновлено, всего: ${books.size}")
                }
            }
    }
    fun loadRatedBooks(db: FirebaseFirestore, uid: String, isCollab: Boolean = false) {
        db.collection("users").document(uid).collection("rated_books")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MyLog", "Ошибка загрузки оценок книг", error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val books = it.documents.mapNotNull { doc -> doc.toObject(RatedBook::class.java) }
                        .map { rated ->
                            Book(
                                id = rated.key,
                                isbn10 = rated.isbn10,
                                title = rated.title?: "",
                                authors = rated.authors,
                                thumbnail = rated.thumbnail,
                                publishedDate = rated.publishedDate?: "Неизвестно",
                                description = rated.description,
                                isRated = true,
                                isBookMark = rated.isBookMark,
                                isFavorite = rated.isFavorite,
                                userRating = rated.userRating
                            )
                        }
                    ratedBooksState.value = books
                    if (isCollab) {
                        ratedBooksMapState.value = books
                            .filter { it.isbn10.isNotEmpty() && it.userRating != null }
                            .associate { it.isbn10 to it.userRating }
                        ratedBooksTripleState.value = books
                            .filter { it.isbn10.isNotEmpty() && it.userRating != null && it.authors?.isNotEmpty() == true }
                            .map {
                                RatedBookTriple(
                                    isbn10 = it.isbn10,
                                    author = it.authors.toString(),
                                    userRating = it.userRating!!
                                )
                            }
                    }
                    Log.d("MyLog", "Триплет книг: ${ratedBooksTripleState.value}")
                    Log.d("MyLog", "Список оценок книг обновлено, всего: ${books.size}")
                }
            }
    }
}
