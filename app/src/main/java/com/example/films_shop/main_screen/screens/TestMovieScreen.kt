package com.example.films_shop.main_screen.screens

import MovieViewModel
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.MovieitemUi
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.business_logic.onFavsMovies
import com.example.films_shop.main_screen.objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.objects.MainScreenDataObject
import com.example.films_shop.main_screen.objects.MovieScreenDataObject
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestMovieScreen(
    navController: NavController,
    movieViewModel: MovieViewModel,
    navData: MovieScreenDataObject,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val movies = movieViewModel.moviePagingFlow.collectAsLazyPagingItems()
    val db = Firebase.firestore
    val moviesListState = remember { mutableStateOf(emptyList<Movie>()) }

    LaunchedEffect(movies.itemSnapshotList) {
        movieViewModel.loadFavoriteMovies(db, navData.uid)
        val movieList = movies.itemSnapshotList.items
        if (movieList.isNotEmpty()) {
            moviesListState.value = movieList
            Log.d("MyLog", "moviesListState загружено: ${movieList.size}")
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
            items(movies.itemCount) { index ->
                movies[index]?.let { movie ->
                    MovieitemUi(
                        movie = movie,
                        onMovieDetailsClick = {
                            navController.navigate(
                                DetailsNavMovieObject(
                                    title = movie.name ?: "Неизвестно",
                                    genre = movie.genres?.joinToString(", ") { it.name }
                                        ?: "Неизвестно",
                                    year = movie.year?.toString() ?: "Неизвестно",
                                    director = "Неизвестно",
                                    description = movie.description ?: "Описание отсутствует",
                                    imageUrl = movie.poster?.url ?: ""
                                )
                            )
                        },
                        onFavoriteClick = {
                            onFavsMovies(db, navData.uid, movie, !movie.isFavorite)
                        }
                    )
                }
            }
        }
    }
}