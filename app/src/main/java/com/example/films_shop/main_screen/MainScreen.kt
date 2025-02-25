package com.example.films_shop.main_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.films_shop.main_screen.api.MovieViewModel
import com.example.films_shop.main_screen.api.apiKey
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.business_logic.getAllFavFilms
import com.example.films_shop.main_screen.business_logic.getAllFavsIds
import com.example.films_shop.main_screen.business_logic.getAllFilms
import com.example.films_shop.main_screen.business_logic.onFavs
import com.example.films_shop.main_screen.data.Favorite
import com.example.films_shop.main_screen.data.Film
import com.example.films_shop.main_screen.login.data_nav.MainScreenDataObject
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    movieViewModel: MovieViewModel,
    onFilmEditClick: (Film) -> Unit,
    onFilmDetailsClick: (Film) -> Unit,
    onExitClick: () -> Unit,
    onAdminClick: () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val filmsListState = remember {
        mutableStateOf(emptyList<Film>())
    }
    val selectedFavFilms = remember {
        mutableStateOf(false)
    }
    val isFavListEmptyState = remember {
        mutableStateOf(false)
    }
    val isAdminState = remember { mutableStateOf(false) }
    val isUserAccountVisible = remember { mutableStateOf(false) }  // Новое состояние для отображения данных пользователя
    val db = remember {
        Firebase.firestore
    }
    val composition = rememberLottieComposition(spec = LottieCompositionSpec.Asset("emptyListAnim.json"))
    LaunchedEffect(Unit) {
        getAllFavsIds(db, navData.uid) { favs ->
            getAllFilms(db, favs) { films ->
                isFavListEmptyState.value = films.isEmpty()
                filmsListState.value = films
            }
        }
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
                        movieViewModel.fetchAndSaveMovies(apiKey)
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
                    },
                    onAccountClick = {
                        isUserAccountVisible.value = true  // При нажатии на "Аккаунт", показываем данные пользователя
                        selectedFavFilms.value = false
                    }
                )
            }
        )
        { paddingValues ->
            if (isUserAccountVisible.value) {  // Если включен режим просмотра аккаунта
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
            } else {  // Если показываем список фильмов
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(filmsListState.value) { film ->
                        FilmListItemUi(
                            isAdminState.value,
                            film,
                            onFilmDetailsClick = { filmdt ->
                                onFilmDetailsClick(filmdt)
                            },
                            onEditClick = {
                                onFilmEditClick(it)
                            },
                            onFavoriteClick = {
                                filmsListState.value = filmsListState.value.map { fm ->
                                    if (fm.key == film.key) {
                                        onFavs(
                                            db,
                                            navData.uid,
                                            Favorite(fm.key),
                                            !fm.isFavorite
                                        )
                                        fm.copy(isFavorite = !fm.isFavorite)
                                    } else {
                                        fm
                                    }
                                }
                                if (selectedFavFilms.value) {
                                    filmsListState.value = filmsListState.value.filter { it.isFavorite }
                                    isFavListEmptyState.value = filmsListState.value.isEmpty()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
