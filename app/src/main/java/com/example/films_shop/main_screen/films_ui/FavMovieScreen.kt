package com.example.films_shop.main_screen.films_ui

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.MovieitemUi
import com.example.films_shop.main_screen.business_logic.onFavsMovies
import com.example.films_shop.main_screen.login.data_nav.MainScreenDataObject
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun FavMovieScreen(
    favoriteMoviesState: MutableState<List<Movie>>, // Передаем State, а не просто список
    onMovieDetailsClick: (Movie) -> Unit,
    db: FirebaseFirestore,
    navData: MainScreenDataObject,
    isFavListEmptyState: MutableState<Boolean>,
    paddingValues: PaddingValues
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding()
        )
    ) {
        items(favoriteMoviesState.value.size) { index ->
            val movie = favoriteMoviesState.value[index]
            MovieitemUi(
                movie = movie,
                onMovieDetailsClick = { onMovieDetailsClick(movie) },
                onFavoriteClick = {
                    Log.d("MyLog", "Удаляем фильм из избранного: ${movie.id}")
                    onFavsMovies(db, navData.uid, movie, !movie.isFavorite)

                    // Обновляем состояние, удаляя фильм из списка
                    favoriteMoviesState.value = favoriteMoviesState.value.filter { it.id != movie.id }

                    // Обновляем состояние пустого списка
                    isFavListEmptyState.value = favoriteMoviesState.value.isEmpty()
                }
            )
        }
    }
}
