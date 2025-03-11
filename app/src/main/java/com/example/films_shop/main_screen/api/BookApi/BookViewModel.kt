package com.example.films_shop.main_screen.api.BookApi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest

class BookViewModel : ViewModel() {
    private val _query = MutableStateFlow("python") // По умолчанию ищем "python"
    val query: StateFlow<String> = _query

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookPagingFlow: Flow<PagingData<Book>> = _query
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = 10, prefetchDistance = 2),
                pagingSourceFactory = { BookPagingSource(RetrofitInstanceBooks.api, query) }
            ).flow.cachedIn(viewModelScope)
        }

    fun searchBooks(newQuery: String) {
        _query.value = newQuery
    }
}
