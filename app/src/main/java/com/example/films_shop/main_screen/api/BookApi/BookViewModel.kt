package com.example.films_shop.main_screen.api.BookApi

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.films_shop.main_screen.business_logic.FavoriteBook
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest

class BookViewModel : ViewModel() {
    private val _query = MutableStateFlow("python") // По умолчанию ищем "python"
    val query: StateFlow<String> = _query
    val favoriteBooksState = mutableStateOf<List<Book>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookPagingFlow: Flow<PagingData<Book>> = _query
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = 10, prefetchDistance = 2),
                pagingSourceFactory = { BookPagingSource(RetrofitInstanceBooks.api, query) }
            ).flow.cachedIn(viewModelScope)
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
                                title = favorite.title?: "",
                                authors = favorite.authors,
                                thumbnail = favorite.thumbnail,
                                publishedDate = favorite.publishedDate?: "Неизвестно",
                                description = favorite.description,
                                isFavorite = true
                            )
                        }
                    favoriteBooksState.value = books
                    Log.d("MyLog", "Избранное обновлено, всего: ${books.size}")
                }
            }
    }
}
