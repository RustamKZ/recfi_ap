package com.example.films_shop.main_screen.films_ui

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.MovieitemUi
import com.example.films_shop.main_screen.api.copySafe
import com.example.films_shop.main_screen.business_logic.onFavsMovies
import com.example.films_shop.main_screen.data.Film
import com.example.films_shop.main_screen.login.data_nav.MainScreenDataObject
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MovieScreen(
    movies: LazyPagingItems<Movie>,
    onMovieDetailsClick: (Movie) -> Unit,
    moviesListState: MutableState<List<Movie>>,
    db: FirebaseFirestore,
    navData: MainScreenDataObject,
    selectedFavFilms: MutableState<Boolean>,
    isFavListEmptyState: MutableState<Boolean>,
    filmsListState: MutableState<List<Film>>,
    paddingValues: PaddingValues
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding()
        )
    ) {
        items(movies.itemCount) { index ->
            val movie = movies[index]
            if (movie != null) {
                MovieitemUi(
                    movie = movie,
                    onMovieDetailsClick = { movie ->
                        Log.d("MyLog", "movie.id: ${movie.id}")
                        onMovieDetailsClick(movie)
                    },
                    onFavoriteClick = {
                        Log.d("MyLog", "movie.id: ${movie.id}")
                        val updatedList = moviesListState.value.map { mv ->
                            Log.d("MyLog", "mv.id: ${mv.id}")
                            Log.d("MyLog", "movie.id: ${movie.id}")
                            if (mv.id == movie.id) {
                                onFavsMovies(
                                    db,
                                    navData.uid,
                                    mv,
                                    !mv.isFavorite
                                )
                                Log.d("MyLog", "BEFORE COPY: ${mv.id}, isFavorite: ${mv.isFavorite}, poster: ${mv.poster}, genres: ${mv.genres}, rating: ${mv.rating}")
                                mv.copySafe(isFavorite = !mv.isFavorite)
                            } else {
                                mv
                            }
                        }.toMutableStateList()
                        moviesListState.value = updatedList // <-- присваиваем обновленный список
                        Log.d("MyLog", "moviesListState size: ${moviesListState.value.size}")
                        if (selectedFavFilms.value) {
                            moviesListState.value =
                                moviesListState.value.filter { it.isFavorite }
                            isFavListEmptyState.value = filmsListState.value.isEmpty()
                        }
                    }
                )
            }
        }

        movies.apply {
            when {
                loadState.append is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
                loadState.refresh is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}