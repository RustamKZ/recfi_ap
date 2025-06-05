package com.example.films_shop.main_screen.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.films_shop.main_screen.api.BookApi.BookItemUi
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.bottom_menu.MainViewModel
import com.example.films_shop.main_screen.objects.main_screens_objects.BookScreenDataObject
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavBookObject
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    navController: NavController,
    bookViewModel: BookViewModel,
    navData: BookScreenDataObject,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior,
    noOpNestedScrollConnection: NestedScrollConnection,
    viewModel: MainViewModel
) {
    val authors = listOf(
        "Пушкин",
        "Достоевский",
        "Стивен Кинг",
        "Лондон",
        "Роулинг"
    )

    val booksFlow = remember(authors) {
        bookViewModel.getBooksByAuthors(authors)
    }
    val books = booksFlow.collectAsLazyPagingItems()

    val db = Firebase.firestore
    LaunchedEffect(books.itemSnapshotList) {
        bookViewModel.loadBookmarkBooks(db, navData.uid)
        bookViewModel.loadFavoriteBooks(db, navData.uid)
        bookViewModel.loadRatedBooks(db, navData.uid)
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
                    email = navData.email,
                    selectedTab = viewModel.selectedTab,
                    onTabSelected = { viewModel.onTabSelected(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(noOpNestedScrollConnection)
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                items(books.itemCount) { index ->
                    val book = books[index]
                    if (book != null) {
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
                                        thumbnail = book.thumbnail ?: "",
                                        publishedDate = book.publishedDate ?: "Неизвестно",
                                        isFavorite = book.isFavorite,
                                        isBookmark = book.isBookMark,
                                        isRated = book.isRated,
                                        userRating = book.userRating,
                                        publisher = book.publisher,
                                        pageCount = book.pageCount,
                                        categories = book.categories?.joinToString(", ")
                                            ?: "Неизвестно",
                                        averageRating = book.averageRating,
                                        ratingsCount = book.ratingsCount,
                                        language = book.language
                                    )
                                )
                            }
                        )
                    }
                }
                if (books.loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

            }
            if (books.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp),
                    strokeWidth = 4.dp
                )
            }
            }
        }
    }