package com.example.films_shop.main_screen.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.films_shop.main_screen.api.BookApi.Book
import com.example.films_shop.main_screen.api.BookApi.BookItemUi
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.MovieitemUi
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.objects.BookScreenDataObject
import com.example.films_shop.main_screen.objects.DetailsNavBookObject
import com.example.films_shop.main_screen.objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.top_bar.TopBarMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    navController: NavController,
    bookViewModel: BookViewModel,
    navData: BookScreenDataObject,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val books = bookViewModel.bookPagingFlow.collectAsLazyPagingItems()
    //val db = Firebase.firestore
    val booksListState = remember { mutableStateOf(emptyList<Book>()) }

    LaunchedEffect(books.itemSnapshotList) {
        //movieViewModel.loadFavoriteMovies(db, navData.uid)
        val bookList = books.itemSnapshotList.items
        if (bookList.isNotEmpty()) {
            booksListState.value = bookList
            Log.d("MyLog", "booksListState загружено: ${bookList.size}")
        }
    }
    Scaffold(
        topBar = {
            if (showTopBar) {
                TopBarMenu(scrollBehavior = scrollBehavior)
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomMenu(
                    navController = navController,
                    uid = navData.uid,
                    email = navData.email
                )
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding( top = innerPadding.calculateTopPadding())
        ) {
            items(books.itemCount) { index ->
                books[index]?.let { book ->
                    BookItemUi(
                        book = book,
                        onBookDetailsClick = {
                            navController.navigate(
                                DetailsNavBookObject(
                                    id = book.id,
                                    isbn10 = book.isbn10,
                                    title = book.title,
                                    authors = book.authors?.joinToString(", ")
                                        ?: "Неизвестно",
                                    description = book.description ?: "Описание отсутствует",
                                    thumbnail = book.thumbnail?: "",
                                    publishedDate = book.publishedDate ?: "Неизвестно",
                                    isFavorite = book.isFavorite
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}