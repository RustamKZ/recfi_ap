package com.example.films_shop.main_screen.screens

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.films_shop.main_screen.api.BookApi.Book
import com.example.films_shop.main_screen.api.BookApi.BookItemUi
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.api.recomendations.RecommendationViewModel
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.bottom_menu.MainViewModel
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavBookObject
import com.example.films_shop.main_screen.objects.rec_objects.RecBookScreenDataObject
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecBookScreen(
    navController: NavController,
    bookViewModel: BookViewModel,
    recViewModel: RecommendationViewModel,
    navData: RecBookScreenDataObject,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior,
    noOpNestedScrollConnection: NestedScrollConnection,
    viewModel: MainViewModel,
    flag: Boolean = false,
    customFlag: Boolean = false
) {
    val recommendationBooksAuthor by recViewModel.recommendationBooksAuthor
    val customBooks by recViewModel.booksDataset
    val recBooks by recViewModel.recommendationCollabBooks
    val content = when(flag) {
        true -> if (customFlag) customBooks else recommendationBooksAuthor
        false -> recBooks
    }
    val books = bookViewModel.bookPagingFlow.collectAsLazyPagingItems()
    val db = Firebase.firestore
    val booksListState = remember { mutableStateOf(emptyList<Book>()) }
    LaunchedEffect(books.itemSnapshotList) {
        bookViewModel.loadBookmarkBooks(db, navData.uid)
        bookViewModel.loadFavoriteBooks(db, navData.uid)
        bookViewModel.loadRatedBooks(db, navData.uid)
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
                    email = navData.email,
                    selectedTab = viewModel.selectedTab,
                    onTabSelected = { viewModel.onTabSelected(it) }
                )
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(noOpNestedScrollConnection)
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            items(content.take(20)) { book ->
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
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(100.dp))
            }
            }
        }
    }