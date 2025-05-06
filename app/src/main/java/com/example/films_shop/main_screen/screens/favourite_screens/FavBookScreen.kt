package com.example.films_shop.main_screen.screens.favourite_screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.films_shop.main_screen.api.BookApi.BookItemUi
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.objects.DetailsNavBookObject
import com.example.films_shop.main_screen.objects.MainScreenDataObject
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun FavBookScreen(
    navData: MainScreenDataObject,
    bookViewModel: BookViewModel,
    navController: NavController,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val favoriteBooksState = remember { bookViewModel.favoriteBooksState }
    val db = Firebase.firestore
    val isFavListEmptyState = remember { mutableStateOf(favoriteBooksState.value.isEmpty()) }
    val composition =
        rememberLottieComposition(spec = LottieCompositionSpec.Asset("emptyListAnim.json"))
    LaunchedEffect(Unit) {
        bookViewModel.loadFavoriteBooks(db, navData.uid)
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
        if (isFavListEmptyState.value) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Список избранного пуст",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                LottieAnimation(
                    composition = composition.value,
                    iterations = LottieConstants.IterateForever
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(favoriteBooksState.value.size) { index ->
                    val book = favoriteBooksState.value[index]
                    BookItemUi(
                        book = book,
                        onBookDetailsClick = {
                            navController.navigate(
                                DetailsNavBookObject(
                                    id = book.id ?: "",
                                    title = book.title ?: "Неизвестно",
                                    isbn10 = book.isbn10,
                                    authors = book.authors?.joinToString(", ")
                                        ?: "Неизвестно",
                                    description = book.description ?: "Описание отсутствует",
                                    thumbnail = book.thumbnail ?: "Неизвестно",
                                    publishedDate = book.publishedDate ?: "",
                                    isFavorite = book.isFavorite,
                                    isBookmark = book.isBookMark,
                                    isRated = book.isRated,
                                    userRating = book.userRating
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}