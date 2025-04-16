package com.example.films_shop.main_screen.screens

import ContentType
import MovieViewModel
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
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.MovieitemUi
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.objects.MovieScreenDataObject
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(
    navController: NavController,
    movieViewModel: MovieViewModel,
    navData: MovieScreenDataObject,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior,
    contentType: ContentType
) {
    LaunchedEffect(contentType) {
        movieViewModel.setContentType(contentType)
    }
    val movies = movieViewModel.currentContentFlow.collectAsLazyPagingItems()

    val db = Firebase.firestore
    val moviesListState = remember { mutableStateOf(emptyList<Movie>()) }

    LaunchedEffect(movies.itemSnapshotList) {
        movieViewModel.loadFavoriteMovies(db, navData.uid, contentType)
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
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            items(movies.itemCount) { index ->
                movies[index]?.let { movie ->
                    MovieitemUi(
                        movie = movie,
                        onMovieDetailsClick = {
                            Log.d("MyLog", "movie.isFavorite ${movie.isFavorite}")
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
                                    rating = movie.rating?.kp ?: 0.0,
                                    persons = movie.persons?.joinToString(", ") { it.name }
                                        ?: "Неизвестно",
                                    isFavorite = movie.isFavorite
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}