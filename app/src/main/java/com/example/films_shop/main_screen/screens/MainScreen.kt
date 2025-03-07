package com.example.films_shop.main_screen.screens

import MovieViewModel
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.films_shop.R
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.objects.MainScreenDataObject
import com.example.films_shop.main_screen.objects.MovieScreenDataObject
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

val custom_font = FontFamily(
    Font(R.font.custom_font, FontWeight.Normal),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    movieViewModel: MovieViewModel,
    navController: NavController,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val moviesListState = remember {
        mutableStateOf(emptyList<Movie>())
    }
    //var favoriteMovies by movieViewModel.favoriteMoviesState
    val favoriteMoviesState = remember { movieViewModel.favoriteMoviesState }
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
        Log.d(
            "MyLog",
            "favoriteMoviesState обновлено, избранных: ${favoriteMoviesState.value.size}"
        )
    }
    val pagerState = rememberPagerState(
        pageCount = { minOf(5, movies.itemCount) }
    )
    val scrollState = rememberScrollState()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            Column(

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Сейчас в тренде",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                    Button(
                        onClick = {
                            Log.d("MyLog", "Click on see all")
                            navController.navigate(
                                MovieScreenDataObject(
                                    navData.uid,
                                    navData.email
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent, // Прозрачный фон
                            contentColor = Color.Black // Цвет текста
                        ),
                        contentPadding = PaddingValues(0.dp), // Убираем отступы внутри кнопки
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .wrapContentSize()
                    ) {
                        Text(
                            text = "Посмотреть",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                        )
                    }

                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(minOf(10, movies.itemCount)) { index ->
                        movies[index]?.let { movie ->
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .width(120.dp)
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = movie.poster?.url,
                                    contentDescription = "Постер фильма",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .clip(RoundedCornerShape(15.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Рекомендуем",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                        Button(
                            onClick = { /* Действие при нажатии */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent, // Прозрачный фон
                                contentColor = Color.Black // Цвет текста
                            ),
                            contentPadding = PaddingValues(0.dp), // Убираем отступы внутри кнопки
                            modifier = Modifier
                                .padding(end = 24.dp)
                                .wrapContentSize()
                        ) {
                            Text(
                                text = "Посмотреть",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                            )
                        }

                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(start = 8.dp, end = 8.dp)
                    ) { index ->
                        movies[index]?.let { movie ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(50.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = movie.poster?.url,
                                    contentDescription = "Постер фильма",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .clip(RoundedCornerShape(15.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            repeat(minOf(5, movies.itemCount)) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(if (pagerState.currentPage == index) 10.dp else 8.dp) // Активная точка больше
                                        .background(
                                            if (pagerState.currentPage == index) Color.DarkGray else Color.LightGray,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                    Column(

                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Сериалы",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                            Button(
                                onClick = {

                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.Black
                                ),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .padding(end = 24.dp)
                                    .wrapContentSize()
                            ) {
                                Text(
                                    text = "Посмотреть",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                                )
                            }

                        }
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(minOf(10, movies.itemCount)) { index ->
                                movies[index]?.let { movie ->
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .width(120.dp)
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(Color.Gray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = movie.poster?.url,
                                            contentDescription = "Постер фильма",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(250.dp)
                                                .clip(RoundedCornerShape(15.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column(

                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Фильмы",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                            Button(
                                onClick = { /* Действие при нажатии */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent, // Прозрачный фон
                                    contentColor = Color.Black // Цвет текста
                                ),
                                contentPadding = PaddingValues(0.dp), // Убираем отступы внутри кнопки
                                modifier = Modifier
                                    .padding(end = 24.dp)
                                    .wrapContentSize()
                            ) {
                                Text(
                                    text = "Посмотреть",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                                )
                            }

                        }
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(minOf(10, movies.itemCount)) { index ->
                                movies[index]?.let { movie ->
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .width(120.dp)
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(Color.Gray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = movie.poster?.url,
                                            contentDescription = "Постер фильма",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(250.dp)
                                                .clip(RoundedCornerShape(15.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
