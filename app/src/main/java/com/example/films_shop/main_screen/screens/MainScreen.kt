package com.example.films_shop.main_screen.screens

import MovieViewModel
import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.films_shop.R
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.objects.MainScreenDataObject
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

val custom_font = FontFamily(
    Font(R.font.custom_font, FontWeight.Normal),
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    movieViewModel: MovieViewModel,
    onExitClick: () -> Unit,
    onMovieDetailsClick: (Movie) -> Unit,
) {
    var scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    val moviesListState = remember {
        mutableStateOf(emptyList<Movie>())
    }
    val selectedFavFilms = remember {
        mutableStateOf(false)
    }
    val isFavListEmptyState = remember {
        mutableStateOf(false)
    }
    //var favoriteMovies by movieViewModel.favoriteMoviesState
    val favoriteMoviesState = remember { movieViewModel.favoriteMoviesState }
    val isUserAccountVisible =
        remember { mutableStateOf(false) }
    val isMovieScreenVisible =
        remember { mutableStateOf(false) }
    val isHomeScreenVisible =
        remember { mutableStateOf(true) }
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
            Log.d(
                "MyLog",
                "moviesListState загружено: ${movieList.size}, null элементов: ${movies.itemSnapshotList.items.count { it == null }}"
            )
        }
    }
    if (favoriteMoviesState.value.isNotEmpty()) {
        Log.d("MyLog", "favoriteMoviesState обновлено, избранных: ${favoriteMoviesState.value.size}")
    }
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopBarMenu(
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                BottomMenu(
                    onHomeClick = {
                        isHomeScreenVisible.value = true
                    },
                    onAccountClick = {
                        isUserAccountVisible.value =
                            true  // При нажатии на "Аккаунт", показываем данные пользователя
                        selectedFavFilms.value = false
                        isMovieScreenVisible.value = false
                        isHomeScreenVisible.value = false
                    },
                    onFavouriteClick = {
                        selectedFavFilms.value = false
                        isUserAccountVisible.value = false
                        isMovieScreenVisible.value = false
                        isHomeScreenVisible.value = false
                    }
                )
            }
        )
        { paddingValues ->
            if (isHomeScreenVisible.value) {
                Log.d("MyLog", "HomeScreen()")
                HomeScreen(
                    onTrendFilmsClick = {
                        isMovieScreenVisible.value = true
                        selectedFavFilms.value = false
                        isHomeScreenVisible.value = false
                    },
                    paddingValues = paddingValues
                )
            } else if (isMovieScreenVisible.value) {
                Log.d("MyLog", "MovieScreen()")
                BackHandler {
                    isMovieScreenVisible.value = false
                    selectedFavFilms.value = false
                    isHomeScreenVisible.value = true
                }
                MovieScreen(
                    movies = movies,
                    onMovieDetailsClick = onMovieDetailsClick,
                    moviesListState = moviesListState,
                    db = db,
                    navData = navData,
                    selectedFavFilms = selectedFavFilms,
                    isFavListEmptyState = isFavListEmptyState,
                    paddingValues = paddingValues,
                )
            } else if (isUserAccountVisible.value) {
                Log.d("MyLog", "UserInfo()")
                AccountDetailsScreen(navData) {
                    onExitClick()
                }
            } else if (isFavListEmptyState.value) {
                Log.d("MyLog", "EmptyList ${favoriteMoviesState.value.size}")
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
                Log.d("MyLog", "FavMovieScreen")
                FavMovieScreen(
                    favoriteMoviesState = favoriteMoviesState,
                    onMovieDetailsClick = onMovieDetailsClick,
                    db = db,
                    navData = navData,
                    isFavListEmptyState = isFavListEmptyState,
                    paddingValues = paddingValues
                )
            }
        }
    }
