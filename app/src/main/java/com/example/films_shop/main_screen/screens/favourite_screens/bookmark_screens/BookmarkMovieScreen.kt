package com.example.films_shop.main_screen.screens.favourite_screens.bookmark_screens

import ContentType
import MovieViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.films_shop.main_screen.api.MovieitemUi
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.bottom_menu.MainViewModel
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun BookmarkMovieScreen(
    navData: MainScreenDataObject,
    movieViewModel: MovieViewModel,
    navController: NavController,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior,
    contentType: ContentType,
) {
    val viewModel: MainViewModel = viewModel()
    val bookmarkContentState = when (contentType) {
        ContentType.MOVIES -> remember { movieViewModel.bookmarkMoviesState }
        ContentType.TV_SERIES -> remember { movieViewModel.bookmarkTvSeriesState }
        ContentType.CARTOONS -> remember { movieViewModel.bookmarkCartoonsState }
    }

    val db = Firebase.firestore
    val isBookmarkListEmptyState = remember { mutableStateOf(bookmarkContentState.value.isEmpty()) }
    val composition =
        rememberLottieComposition(spec = LottieCompositionSpec.Asset("emptyListAnim.json"))
    LaunchedEffect(Unit) {
        movieViewModel.loadBookmarkMovies(db, navData.uid, contentType)
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
        if (isBookmarkListEmptyState.value) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Список посмотреть позже пуст",
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
                items(bookmarkContentState.value.size) { index ->
                    val movie = bookmarkContentState.value[index]
                    MovieitemUi(
                        movie = movie,
                        onMovieDetailsClick = {
                            navController.navigate(
                                DetailsNavMovieObject(
                                    id = movie.id ?: "",
                                    tmdbId = movie.externalId?.tmdb ?: 0,
                                    title = movie.name ?: "Неизвестно",
                                    type = movie.type ?: "Неизвестно",
                                    genre = movie.genres?.joinToString(", ") { it.name }
                                        ?: "Неизвестно",
                                    year = movie.year ?: "Неизвестно",
                                    description = movie.description ?: "Описание отсутствует",
                                    imageUrl = movie.poster?.url ?: "",
                                    backdropUrl = movie.backdrop?.url ?: "",
                                    persons = movie.persons?.joinToString(", ") { "${it.name}|${it.photo}" }
                                        ?: "Неизвестно",
                                    ratingKp = movie.rating?.kp ?: 0.0,
                                    ratingImdb = movie.rating?.imdb ?: 0.0,
                                    votesKp = movie.votes?.kp ?: 0,
                                    votesImdb = movie.votes?.imdb ?: 0,
                                    isFavorite = movie.isFavorite,
                                    isBookMark = movie.isBookMark,
                                    isRated = movie.isRated,
                                    userRating = movie.userRating ?: 0
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
}