package com.example.films_shop.main_screen.screens

import MovieViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.films_shop.main_screen.api.MovieitemUi
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.business_logic.onFavsMovies
import com.example.films_shop.main_screen.objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.objects.MainScreenDataObject
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun TestFavMovieScreen(
    navData: MainScreenDataObject,
    movieViewModel: MovieViewModel,
    navController: NavController,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val favoriteMoviesState = remember { movieViewModel.favoriteMoviesState }
    val db = Firebase.firestore
    val isFavListEmptyState = remember { mutableStateOf(favoriteMoviesState.value.isEmpty()) }

    LaunchedEffect(Unit) {
        movieViewModel.loadFavoriteMovies(db, navData.uid)
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
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(favoriteMoviesState.value.size) { index ->
                    val movie = favoriteMoviesState.value[index]
                    MovieitemUi(
                        movie = movie,
                        onMovieDetailsClick = { movie ->
                            navController.navigate(
                                DetailsNavMovieObject(
                                    title = movie.name ?: "Неизвестно",
                                    genre = movie.genres?.joinToString(", ") { it.name }
                                        ?: "Неизвестно",
                                    year = movie.year ?: "Неизвестно",
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