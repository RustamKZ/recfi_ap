package com.example.films_shop.main_screen

import MovieViewModel
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.MovieitemUi
import com.example.films_shop.main_screen.api.copySafe
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.business_logic.getAllFavFilms
import com.example.films_shop.main_screen.business_logic.getAllFavsIds
import com.example.films_shop.main_screen.business_logic.getAllFilms
import com.example.films_shop.main_screen.business_logic.onFavs
import com.example.films_shop.main_screen.business_logic.onFavsMovies
import com.example.films_shop.main_screen.data.Favorite
import com.example.films_shop.main_screen.data.FavoriteMovie
import com.example.films_shop.main_screen.data.Film
import com.example.films_shop.main_screen.login.data_nav.MainScreenDataObject
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    movieViewModel: MovieViewModel,
    onFilmEditClick: (Film) -> Unit,
    onFilmDetailsClick: (Film) -> Unit,
    onExitClick: () -> Unit,
    onMovieDetailsClick: (Movie) -> Unit,
    onAdminClick: () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val filmsListState = remember {
        mutableStateOf(emptyList<Film>())
    }
    val moviesListState = remember {
            mutableStateOf(emptyList<Movie>())
    }
    val selectedFavFilms = remember {
        mutableStateOf(false)
    }
    val isFavListEmptyState = remember {
        mutableStateOf(false)
    }
    var favoriteMovies by movieViewModel.favoriteMoviesState
    val isAdminState = remember { mutableStateOf(false) }
    val isUserAccountVisible =
        remember { mutableStateOf(false) }  // Новое состояние для отображения данных пользователя
    val isApiTestVisible =
        remember { mutableStateOf(false) } // Состояние для тестирования  показа API
    val movies = movieViewModel.moviePagingFlow.collectAsLazyPagingItems()
    val db = remember {
        Firebase.firestore
    }
    val composition =
        rememberLottieComposition(spec = LottieCompositionSpec.Asset("emptyListAnim.json"))
    LaunchedEffect(movies.itemSnapshotList) {
        movieViewModel.loadFavoriteMovies(db, navData.uid)
        val movieList = movies.itemSnapshotList.items
        if (movieList.isNotEmpty()) {
            moviesListState.value = movieList
            Log.d("MyLog", "moviesListState загружено: ${movieList.size}, null элементов: ${movies.itemSnapshotList.items.count { it == null }}")
        }

        getAllFavsIds(db, navData.uid) { favs ->
            getAllFilms(db, favs) { films ->
                isFavListEmptyState.value = films.isEmpty()
                filmsListState.value = films
            }
        }
    }
    if (favoriteMovies.isNotEmpty()) {
        Log.d("MyLog", "moviesListState обновлено, избранных: ${favoriteMovies.size}")
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = Modifier.fillMaxWidth(),
        drawerContent = {
            Column(
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            {
                DrawerHeader(navData.email)
                DrawerBody(
                    onAdmin = { isAdmin ->
                        isAdminState.value = isAdmin
                    },
                    onApiClick = {

                    },
                    onFavClick = {
                        selectedFavFilms.value = true
                        getAllFavsIds(db, navData.uid) { favs ->
                            getAllFavFilms(db, favs) { films ->
                                isFavListEmptyState.value = films.isEmpty()
                                filmsListState.value = films
                            }
                        }
                        coroutineScope.launch { drawerState.close() }
                    },
                    onAdminClick = {
                        coroutineScope.launch { drawerState.close() }
                        selectedFavFilms.value = false
                        onAdminClick()
                    },
                    onExitClick = {
                        coroutineScope.launch { drawerState.close() }
                        onExitClick()
                    },
                    onGenreClick = { genre ->
                        getAllFavsIds(db, navData.uid) { favs ->
                            if (genre == "All") {
                                getAllFilms(db, favs) { films ->
                                    isFavListEmptyState.value = films.isEmpty()
                                    filmsListState.value = films
                                }
                            } else {
                                getAllFilms(db, favs, genre, true) { films ->
                                    isFavListEmptyState.value = films.isEmpty()
                                    filmsListState.value = films
                                }
                            }
                        }
                        selectedFavFilms.value = false
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomMenu(
                    onHomeClick = {
                        selectedFavFilms.value = false
                        isUserAccountVisible.value = false
                        getAllFavsIds(db, navData.uid) { favs ->
                            getAllFilms(db, favs, "") { films ->
                                isFavListEmptyState.value = films.isEmpty()
                                filmsListState.value = films
                            }
                        }
                        isApiTestVisible.value = false
                    },
                    onAccountClick = {
                        isUserAccountVisible.value =
                            true  // При нажатии на "Аккаунт", показываем данные пользователя
                        selectedFavFilms.value = false
                        isApiTestVisible.value = false
                    },
                    onApiTestClick = {
                        isApiTestVisible.value = true
                        selectedFavFilms.value = false
                    }
                )
            }
        )
        {
            if (isApiTestVisible.value) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(8.dp)
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
            else if (isUserAccountVisible.value) {  // Если включен режим просмотра аккаунта
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Здесь можно вывести данные пользователя
                    Text(
                        text = "User Info",
                        fontFamily = custom_font
                    )
                    Text(
                        text = "Email: ${navData.email}",
                        fontFamily = custom_font
                    )
                    // Другие поля с данными пользователя
                }
            } else if (isFavListEmptyState.value) {  // Если список фильмов пуст
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(top = 100.dp),
                        text = "Empty List",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = custom_font
                    )
                    LottieAnimation(
                        composition = composition.value,
                        iterations = LottieConstants.IterateForever
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                ) {
                    items(favoriteMovies.size) { index ->
                        val movie = favoriteMovies[index]
                        MovieitemUi(
                            movie = movie,
                            onMovieDetailsClick = { movie ->
                                Log.d("MyLog", "movie.id: ${movie.id}")
                                onMovieDetailsClick(movie)
                            },
                            onFavoriteClick = {
                                Log.d("MyLog", "movie.id: ${movie.id}")
                                onFavsMovies(db, navData.uid, movie, !movie.isFavorite)
                                favoriteMovies =
                                    favoriteMovies.filter { it.id != movie.id }
                                isFavListEmptyState.value = favoriteMovies.isEmpty()
                            }
                        )
                    }
                }
            }
        }
    }
}
