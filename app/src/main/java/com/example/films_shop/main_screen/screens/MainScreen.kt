package com.example.films_shop.main_screen.screens

import MovieViewModel
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import com.example.films_shop.main_screen.Genres.saveSelectedGenres
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.films_shop.R
import com.example.films_shop.main_screen.Genres.GenreKP
import com.example.films_shop.main_screen.Genres.genres
import com.example.films_shop.main_screen.Genres.loadUserGenres
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.api.recomendations.RecommendationViewModel
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.objects.main_screens_objects.BookScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.CartoonScreenDataObject
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavBookObject
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MovieScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.SeriesScreenDataObject
import com.example.films_shop.main_screen.objects.rec_objects.RecBookScreenDataObject
import com.example.films_shop.main_screen.objects.rec_objects.RecCartoonScreenDataObject
import com.example.films_shop.main_screen.objects.rec_objects.RecMovieScreenDataObject
import com.example.films_shop.main_screen.objects.rec_objects.RecTvSeriesScreenDataObject
import com.example.films_shop.main_screen.top_bar.TopBarMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

val custom_font = FontFamily(
    Font(R.font.custom_font, FontWeight.Normal),
)
val test_font = FontFamily(
    Font(R.font.lumiosmarker_0, FontWeight.Normal),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    movieViewModel: MovieViewModel,
    bookViewModel: BookViewModel,
    recViewModel: RecommendationViewModel,
    navController: NavController,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior,
    noOpNestedScrollConnection:  NestedScrollConnection
) {
    // ui theme
    val isDark = isSystemInDarkTheme()
    val iconColor = if (isDark) Color.White else Color.Gray
    // ui theme
    var showOverlay by remember { mutableStateOf(false) }
    val selectedGenres = remember { mutableStateListOf<GenreKP>() }
    // Инициализируем состояние анимации
    val showLoadingAnimation = remember { mutableStateOf(navData.showLoadingAnimation) }
    val composition =
        rememberLottieComposition(spec = LottieCompositionSpec.Asset("start_anim.json"))
    val alpha = remember { Animatable(1f) }
    val dataLoaded = remember { mutableStateOf(false) }
    val movies = movieViewModel.moviesPagingFlow.collectAsLazyPagingItems()
    val series = movieViewModel.tvSeriesPagingFlow.collectAsLazyPagingItems()
    val cartoons = movieViewModel.cartoonsPagingFlow.collectAsLazyPagingItems()
    val authors = listOf("Пушкин", "Ремарк")
    val booksFlow = remember(authors) {
        bookViewModel.getBooksByAuthors(authors)
    }
    val books = booksFlow.collectAsLazyPagingItems()
    val db = remember {
        Firebase.firestore
    }
    // Коллаборативная фильтрация рекомендации
    LaunchedEffect(Unit) {
        movieViewModel.loadRatedMovies(db, navData.uid, isCollab = true)
        bookViewModel.loadRatedBooks(db, navData.uid, isCollab = true)

        launch {
            movieViewModel.ratedMoviesMapState
                .filter { it.isNotEmpty() }
                .collectLatest {
                    Log.d("Debug", "Movies map collected: $it")
                    recViewModel.fetchCollabRecommendationsFilmsCartoonSeries(it, "movie")
                }
        }

        launch {
            movieViewModel.ratedCartoonsMapState
                .filter { it.isNotEmpty() }
                .collectLatest {
                    Log.d("Debug", "Cartoon map collected: $it")
                    recViewModel.fetchCollabRecommendationsFilmsCartoonSeries(it, "cartoon")
                }
        }

        launch {
            movieViewModel.ratedTvSeriesMapState
                .filter { it.isNotEmpty() }
                .collectLatest {
                    Log.d("Debug", "TV SERIES map collected: $it")
                    recViewModel.fetchCollabRecommendationsFilmsCartoonSeries(it, "tv-series")
                }
        }

        launch {
            bookViewModel.ratedBooksMapState
                .filter { it.isNotEmpty() }
                .collectLatest {
                    Log.d("Debug", "Books collected: $it")
                    recViewModel.fetchCollabRecommendationsBooks(it, bookViewModel)
                }
        }
    }

    val recommendationMovies by recViewModel.recommendationCollabMovies
    val recommendationCartoon by recViewModel.recommendationCollabCartoon
    val recommendationTvSeries by recViewModel.recommendationCollabTvSeries
    val recommendationBooks by recViewModel.recommendationCollabBooks
    // Коллаборативная фильтрация рекомендации
    LaunchedEffect(navData.uid) {
        loadUserGenres(db, navData.uid) { loadedGenres ->
            if (loadedGenres.isEmpty()) {
                showOverlay = true
            } else {
                selectedGenres.addAll(loadedGenres)
                showOverlay = false
            }
        }
    }
    LaunchedEffect(movies.itemSnapshotList) {
        if (movies.itemCount > 0) {
            dataLoaded.value = true
        }
        movieViewModel.loadFavoriteMovies(db, navData.uid)
        movieViewModel.loadBookmarkMovies(db, navData.uid)
        movieViewModel.loadRatedMovies(db, navData.uid)
    }
    LaunchedEffect(books.itemSnapshotList) {
        if (books.itemCount > 0) {
            dataLoaded.value = true
        }
        bookViewModel.loadFavoriteBooks(db, navData.uid)
        bookViewModel.loadBookmarkBooks(db, navData.uid)
        bookViewModel.loadRatedBooks(db, navData.uid)
    }
    // Управляем анимацией и её завершением
    LaunchedEffect(key1 = showLoadingAnimation.value, key2 = dataLoaded.value) {
        if (showLoadingAnimation.value) {
            // Минимальное время показа анимации
            delay(2000)

            // Даже если прошло 2 секунды, ждем загрузки данных
            // но с ограничением по времени в 5 секунд
            withTimeoutOrNull(3000) {
                while (!dataLoaded.value) {
                    delay(100)
                }
            }

            // Плавное скрытие анимации
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(500)
            )

            showLoadingAnimation.value = false
        }
    }
    val pagerState = rememberPagerState(
        pageCount = { minOf(5, movies.itemCount) }
    )
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
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
                    .nestedScroll(if (showTopBar) scrollBehavior.nestedScrollConnection else remember { noOpNestedScrollConnection })
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
                            text = "Фильмы",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                        Button(
                            onClick = {
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
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = "Посмотреть",
                                tint = iconColor
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
                                        .width(170.dp)
                                        .height(250.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(Color.Gray)
                                        .clickable {
                                            navController.navigate(
                                                DetailsNavMovieObject(
                                                    id = movie.id ?: "",
                                                    tmdbId = movie.externalId?.tmdb ?: 0,
                                                    title = movie.name ?: "Неизвестно",
                                                    type = movie.type ?: "Неизвестно",
                                                    genre = movie.genres?.joinToString(", ") { it.name }
                                                        ?: "Неизвестно",
                                                    year = movie.year ?: "Неизвестно",
                                                    description = movie.description
                                                        ?: "Описание отсутствует",
                                                    imageUrl = movie.poster?.url ?: "",
                                                    backdropUrl = movie.backdrop?.url ?: "",
                                                    ratingKp = movie.rating?.kp ?: 0.0,
                                                    ratingImdb = movie.rating?.imdb ?: 0.0,
                                                    votesKp = movie.votes?.kp ?: 0,
                                                    votesImdb = movie.votes?.imdb ?: 0,
                                                    persons = movie.persons?.joinToString(", ") { "${it.name}|${it.photo}" }
                                                        ?: "Неизвестно",
                                                    isFavorite = movie.isFavorite,
                                                    isBookMark = movie.isBookMark,
                                                    isRated = movie.isRated,
                                                    userRating = movie.userRating ?: 0
                                                )
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box {
                                        AsyncImage(
                                            model = movie.poster?.url,
                                            contentDescription = "Постер фильма",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(250.dp)
                                                .clip(RoundedCornerShape(15.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        // Оценка
                                        val rating = movie.rating?.kp ?: 0.0
                                        val backgroundColor = when {
                                            rating > 7 -> colorResource(id = R.color.kp_rating)
                                            rating >= 5 -> Color(0xFFFF9800)
                                            else -> Color(0xFFF44336)
                                        }

                                        Text(
                                            text = String.format("%.1f", rating),
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .background(
                                                    color = backgroundColor,
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 2.dp)
                                                .align(Alignment.TopStart)
                                        )
                                    }
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
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                            Button(
                                onClick = {
                                    MovieScreenDataObject(
                                        navData.uid,
                                        navData.email
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
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = "Посмотреть",
                                    tint = iconColor
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
                                        model = movie.backdrop?.url,
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Фильмы для вас",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                            Button(
                                onClick = {
                                    navController.navigate(
                                        RecMovieScreenDataObject(
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
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = "Посмотреть",
                                    tint = iconColor
                                )
                            }

                        }
                        if (recommendationMovies.isEmpty()) {
                            Text(
                                text = "Пусто",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(recommendationMovies.take(10)) { movie ->
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .width(170.dp)
                                            .height(250.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(Color.Gray)
                                            .clickable {
                                                navController.navigate(
                                                    DetailsNavMovieObject(
                                                        id = movie.id ?: "",
                                                        tmdbId = movie.externalId?.tmdb ?: 0,
                                                        title = movie.name ?: "Неизвестно",
                                                        type = movie.type ?: "Неизвестно",
                                                        genre = movie.genres?.joinToString(", ") { it.name }
                                                            ?: "Неизвестно",
                                                        year = movie.year ?: "Неизвестно",
                                                        description = movie.description
                                                            ?: "Описание отсутствует",
                                                        imageUrl = movie.poster?.url ?: "",
                                                        backdropUrl = movie.backdrop?.url ?: "",
                                                        ratingKp = movie.rating?.kp ?: 0.0,
                                                        ratingImdb = movie.rating?.imdb ?: 0.0,
                                                        votesKp = movie.votes?.kp ?: 0,
                                                        votesImdb = movie.votes?.imdb ?: 0,
                                                        persons = movie.persons?.joinToString(", ") { "${it.name}|${it.photo}" }
                                                            ?: "Неизвестно",
                                                        isFavorite = movie.isFavorite,
                                                        isBookMark = movie.isBookMark,
                                                        isRated = movie.isRated,
                                                        userRating = movie.userRating ?: 0
                                                    )
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box {
                                            AsyncImage(
                                                model = movie.poster?.url,
                                                contentDescription = "Постер фильма",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .clip(RoundedCornerShape(15.dp)),
                                                contentScale = ContentScale.Crop
                                            )

                                            // Оценка
                                            val rating = movie.rating?.kp ?: 0.0
                                            val backgroundColor = when {
                                                rating > 7 -> colorResource(id = R.color.kp_rating)
                                                rating >= 5 -> Color(0xFFFF9800)
                                                else -> Color(0xFFF44336)
                                            }

                                            Text(
                                                text = String.format("%.1f", rating),
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .background(
                                                        color = backgroundColor,
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 10.dp, vertical = 2.dp)
                                                    .align(Alignment.TopStart)
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
                                    text = "Сериалы",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 24.dp)
                                )
                                Button(
                                    onClick = {
                                        navController.navigate(
                                            SeriesScreenDataObject(
                                                navData.uid,
                                                navData.email
                                            )
                                        )
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
                                    Icon(
                                        imageVector = Icons.Outlined.ChevronRight,
                                        contentDescription = "Посмотреть",
                                        tint = iconColor
                                    )
                                }

                            }
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(minOf(10, series.itemCount)) { index ->
                                    series[index]?.let { movie ->
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .width(170.dp)
                                                .height(250.dp)
                                                .clip(RoundedCornerShape(15.dp))
                                                .background(Color.Gray)
                                                .clickable {
                                                    navController.navigate(
                                                        DetailsNavMovieObject(
                                                            id = movie.id ?: "",
                                                            tmdbId = movie.externalId?.tmdb ?: 0,
                                                            title = movie.name ?: "Неизвестно",
                                                            type = movie.type ?: "Неизвестно",
                                                            genre = movie.genres?.joinToString(", ") { it.name }
                                                                ?: "Неизвестно",
                                                            year = movie.year ?: "Неизвестно",
                                                            description = movie.description
                                                                ?: "Описание отсутствует",
                                                            imageUrl = movie.poster?.url ?: "",
                                                            backdropUrl = movie.backdrop?.url ?: "",
                                                            ratingKp = movie.rating?.kp ?: 0.0,
                                                            ratingImdb = movie.rating?.imdb ?: 0.0,
                                                            votesKp = movie.votes?.kp ?: 0,
                                                            votesImdb = movie.votes?.imdb ?: 0,
                                                            persons = movie.persons?.joinToString(", ") { "${it.name}|${it.photo}" }
                                                                ?: "Неизвестно",
                                                            isFavorite = movie.isFavorite,
                                                            isBookMark = movie.isBookMark,
                                                            isRated = movie.isRated,
                                                            userRating = movie.userRating ?: 0
                                                        )
                                                    )
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box {
                                                AsyncImage(
                                                    model = movie.poster?.url,
                                                    contentDescription = "Постер фильма",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(250.dp)
                                                        .clip(RoundedCornerShape(15.dp)),
                                                    contentScale = ContentScale.Crop
                                                )

                                                // Оценка
                                                val rating = movie.rating?.kp ?: 0.0
                                                val backgroundColor = when {
                                                    rating > 6.5 -> colorResource(id = R.color.kp_rating)
                                                    rating >= 4 -> Color(0xFFFF9800)
                                                    else -> Color(0xFFF44336)
                                                }

                                                Text(
                                                    text = String.format("%.1f", rating),
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .padding(8.dp)
                                                        .background(
                                                            color = backgroundColor,
                                                            shape = RoundedCornerShape(6.dp)
                                                        )
                                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                                        .align(Alignment.TopStart)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Сериалы для вас",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                            Button(
                                onClick = {
                                    navController.navigate(
                                        RecTvSeriesScreenDataObject(
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
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = "Посмотреть",
                                    tint = iconColor
                                )
                            }

                        }
                        if (recommendationTvSeries.isEmpty()) {
                            Text(
                                text = "Пусто",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(recommendationTvSeries.take(10)) { movie ->
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .width(170.dp)
                                            .height(250.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(Color.Gray)
                                            .clickable {
                                                navController.navigate(
                                                    DetailsNavMovieObject(
                                                        id = movie.id ?: "",
                                                        tmdbId = movie.externalId?.tmdb ?: 0,
                                                        title = movie.name ?: "Неизвестно",
                                                        type = movie.type ?: "Неизвестно",
                                                        genre = movie.genres?.joinToString(", ") { it.name }
                                                            ?: "Неизвестно",
                                                        year = movie.year ?: "Неизвестно",
                                                        description = movie.description
                                                            ?: "Описание отсутствует",
                                                        imageUrl = movie.poster?.url ?: "",
                                                        backdropUrl = movie.backdrop?.url ?: "",
                                                        ratingKp = movie.rating?.kp ?: 0.0,
                                                        ratingImdb = movie.rating?.imdb ?: 0.0,
                                                        votesKp = movie.votes?.kp ?: 0,
                                                        votesImdb = movie.votes?.imdb ?: 0,
                                                        persons = movie.persons?.joinToString(", ") { "${it.name}|${it.photo}" }
                                                            ?: "Неизвестно",
                                                        isFavorite = movie.isFavorite,
                                                        isBookMark = movie.isBookMark,
                                                        isRated = movie.isRated,
                                                        userRating = movie.userRating ?: 0
                                                    )
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box {
                                            AsyncImage(
                                                model = movie.poster?.url,
                                                contentDescription = "Постер фильма",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .clip(RoundedCornerShape(15.dp)),
                                                contentScale = ContentScale.Crop
                                            )

                                            // Оценка
                                            val rating = movie.rating?.kp ?: 0.0
                                            val backgroundColor = when {
                                                rating > 7 -> colorResource(id = R.color.kp_rating)
                                                rating >= 5 -> Color(0xFFFF9800)
                                                else -> Color(0xFFF44336)
                                            }

                                            Text(
                                                text = String.format("%.1f", rating),
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .background(
                                                        color = backgroundColor,
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 10.dp, vertical = 2.dp)
                                                    .align(Alignment.TopStart)
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
                                    text = "Мультфильмы",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 24.dp)
                                )
                                Button(
                                    onClick = {
                                        navController.navigate(
                                            CartoonScreenDataObject(
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
                                    Icon(
                                        imageVector = Icons.Outlined.ChevronRight,
                                        contentDescription = "Посмотреть",
                                        tint = iconColor
                                    )
                                }

                            }
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(minOf(10, cartoons.itemCount)) { index ->
                                    cartoons[index]?.let { movie ->
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .width(170.dp)
                                                .height(250.dp)
                                                .clip(RoundedCornerShape(15.dp))
                                                .background(Color.Gray)
                                                .clickable {
                                                    navController.navigate(
                                                        DetailsNavMovieObject(
                                                            id = movie.id ?: "",
                                                            tmdbId = movie.externalId?.tmdb ?: 0,
                                                            title = movie.name ?: "Неизвестно",
                                                            type = movie.type ?: "Неизвестно",
                                                            genre = movie.genres?.joinToString(", ") { it.name }
                                                                ?: "Неизвестно",
                                                            year = movie.year ?: "Неизвестно",
                                                            description = movie.description
                                                                ?: "Описание отсутствует",
                                                            imageUrl = movie.poster?.url ?: "",
                                                            backdropUrl = movie.backdrop?.url ?: "",
                                                            ratingKp = movie.rating?.kp ?: 0.0,
                                                            ratingImdb = movie.rating?.imdb ?: 0.0,
                                                            votesKp = movie.votes?.kp ?: 0,
                                                            votesImdb = movie.votes?.imdb ?: 0,
                                                            persons = movie.persons?.joinToString(", ") { "${it.name}|${it.photo}" }
                                                                ?: "Неизвестно",
                                                            isFavorite = movie.isFavorite,
                                                            isBookMark = movie.isBookMark,
                                                            isRated = movie.isRated,
                                                            userRating = movie.userRating ?: 0
                                                        )
                                                    )
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box {
                                                AsyncImage(
                                                    model = movie.poster?.url,
                                                    contentDescription = "Постер фильма",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(250.dp)
                                                        .clip(RoundedCornerShape(15.dp)),
                                                    contentScale = ContentScale.Crop
                                                )

                                                // Оценка
                                                val rating = movie.rating?.kp ?: 0.0
                                                val backgroundColor = when {
                                                    rating > 7 -> colorResource(id = R.color.kp_rating)
                                                    rating >= 5 -> Color(0xFFFF9800)
                                                    else -> Color(0xFFF44336)
                                                }

                                                Text(
                                                    text = String.format("%.1f", rating),
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .padding(8.dp)
                                                        .background(
                                                            color = backgroundColor,
                                                            shape = RoundedCornerShape(6.dp)
                                                        )
                                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                                        .align(Alignment.TopStart)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Мультфильмы для вас",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                            Button(
                                onClick = {
                                    navController.navigate(
                                        RecCartoonScreenDataObject(
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
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = "Посмотреть",
                                    tint = iconColor
                                )
                            }

                        }
                        if (recommendationCartoon.isEmpty()) {
                            Text(
                                text = "Пусто",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(recommendationCartoon.take(10)) { movie ->
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .width(170.dp)
                                            .height(250.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(Color.Gray)
                                            .clickable {
                                                navController.navigate(
                                                    DetailsNavMovieObject(
                                                        id = movie.id ?: "",
                                                        tmdbId = movie.externalId?.tmdb ?: 0,
                                                        title = movie.name ?: "Неизвестно",
                                                        type = movie.type ?: "Неизвестно",
                                                        genre = movie.genres?.joinToString(", ") { it.name }
                                                            ?: "Неизвестно",
                                                        year = movie.year ?: "Неизвестно",
                                                        description = movie.description
                                                            ?: "Описание отсутствует",
                                                        imageUrl = movie.poster?.url ?: "",
                                                        backdropUrl = movie.backdrop?.url ?: "",
                                                        ratingKp = movie.rating?.kp ?: 0.0,
                                                        ratingImdb = movie.rating?.imdb ?: 0.0,
                                                        votesKp = movie.votes?.kp ?: 0,
                                                        votesImdb = movie.votes?.imdb ?: 0,
                                                        persons = movie.persons?.joinToString(", ") { "${it.name}|${it.photo}" }
                                                            ?: "Неизвестно",
                                                        isFavorite = movie.isFavorite,
                                                        isBookMark = movie.isBookMark,
                                                        isRated = movie.isRated,
                                                        userRating = movie.userRating ?: 0
                                                    )
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box {
                                            AsyncImage(
                                                model = movie.poster?.url,
                                                contentDescription = "Постер фильма",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .clip(RoundedCornerShape(15.dp)),
                                                contentScale = ContentScale.Crop
                                            )

                                            // Оценка
                                            val rating = movie.rating?.kp ?: 0.0
                                            val backgroundColor = when {
                                                rating > 7 -> colorResource(id = R.color.kp_rating)
                                                rating >= 5 -> Color(0xFFFF9800)
                                                else -> Color(0xFFF44336)
                                            }

                                            Text(
                                                text = String.format("%.1f", rating),
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .background(
                                                        color = backgroundColor,
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 10.dp, vertical = 2.dp)
                                                    .align(Alignment.TopStart)
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
                                    text = "Книги",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 24.dp)
                                )
                                Button(
                                    onClick = {
                                        navController.navigate(
                                            BookScreenDataObject(
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
                                    Icon(
                                        imageVector = Icons.Outlined.ChevronRight,
                                        contentDescription = "Посмотреть",
                                        tint = iconColor
                                    )
                                }

                            }
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(minOf(10, books.itemCount)) { index ->
                                    books[index]?.let { book ->
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .width(170.dp)
                                                .height(250.dp)
                                                .clip(RoundedCornerShape(15.dp))
                                                .background(Color.Gray)
                                                .clickable {
                                                    navController.navigate(
                                                        DetailsNavBookObject(
                                                            id = book.id,
                                                            isbn10 = book.isbn10,
                                                            title = book.title,
                                                            authors = book.authors?.joinToString(", ")
                                                                ?: "Неизвестно",
                                                            description = book.description
                                                                ?: "Описание отсутствует",
                                                            thumbnail = book.thumbnail ?: "",
                                                            publishedDate = book.publishedDate
                                                                ?: "Неизвестно",
                                                            isFavorite = book.isFavorite
                                                        )
                                                    )
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box {
                                                AsyncImage(
                                                    model = book.thumbnail?.replace(
                                                        "http://",
                                                        "https://"
                                                    ),
                                                    contentDescription = "Обложка книги",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(250.dp)
                                                        .clip(RoundedCornerShape(15.dp)),
                                                    contentScale = ContentScale.Crop
                                                )

                                                // Оценка
                                                val rating = book.userRating
                                                val backgroundColor = when {
                                                    rating > 7 -> colorResource(id = R.color.kp_rating)
                                                    rating >= 5 -> Color(0xFFFF9800)
                                                    else -> Color(0xFFF44336)
                                                }
                                                if (rating != -1) {
                                                    Text(
                                                        text = String.format("%.1f", rating),
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier
                                                            .padding(8.dp)
                                                            .background(
                                                                color = backgroundColor,
                                                                shape = RoundedCornerShape(6.dp)
                                                            )
                                                            .padding(horizontal = 10.dp, vertical = 2.dp)
                                                            .align(Alignment.TopStart)
                                                    )
                                                }
                                            }
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
                                    text = "Книги для вас",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 24.dp)
                                )
                                Button(
                                    onClick = {
                                        navController.navigate(
                                            RecBookScreenDataObject(
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
                                    Icon(
                                        imageVector = Icons.Outlined.ChevronRight,
                                        contentDescription = "Посмотреть",
                                        tint = iconColor
                                    )
                                }

                            }
                            if (recommendationBooks.isEmpty()) {
                                Text(
                                    text = "Пусто",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(recommendationBooks.take(10)) { book ->
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .width(170.dp)
                                                .height(250.dp)
                                                .clip(RoundedCornerShape(15.dp))
                                                .background(Color.Gray)
                                                .clickable {
                                                    navController.navigate(
                                                        DetailsNavBookObject(
                                                            id = book.id,
                                                            isbn10 = book.isbn10,
                                                            title = book.title,
                                                            authors = book.authors?.joinToString(
                                                                ", "
                                                            )
                                                                ?: "Неизвестно",
                                                            description = book.description
                                                                ?: "Описание отсутствует",
                                                            thumbnail = book.thumbnail ?: "",
                                                            publishedDate = book.publishedDate
                                                                ?: "Неизвестно",
                                                            isFavorite = book.isFavorite
                                                        )
                                                    )
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box {
                                                AsyncImage(
                                                    model = book.thumbnail?.replace(
                                                        "http://",
                                                        "https://"
                                                    ),
                                                    contentDescription = "Обложка книги",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(250.dp)
                                                        .clip(RoundedCornerShape(15.dp)),
                                                    contentScale = ContentScale.Crop
                                                )

                                                // Оценка
                                                val rating = book.userRating
                                                val backgroundColor = when {
                                                    rating > 7 -> colorResource(id = R.color.kp_rating)
                                                    rating >= 5 -> Color(0xFFFF9800)
                                                    else -> Color(0xFFF44336)
                                                }
                                                if (rating != -1) {
                                                    Text(
                                                        text = String.format("%.1f", rating),
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier
                                                            .padding(8.dp)
                                                            .background(
                                                                color = backgroundColor,
                                                                shape = RoundedCornerShape(6.dp)
                                                            )
                                                            .padding(horizontal = 10.dp, vertical = 2.dp)
                                                            .align(Alignment.TopStart)
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
        }
        // Экран поверх
        AnimatedVisibility(
            visible = showOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genres) { genre ->
                        val isSelected = selectedGenres.contains(genre)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSelected) selectedGenres.remove(genre)
                                    else selectedGenres.add(genre)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color.Gray else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                Text(genre.name, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        showOverlay = false
                        saveSelectedGenres(db, navData.uid, selectedGenres)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Сохранить")
                }
            }
        }
        if (showLoadingAnimation.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .alpha(alpha.value),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition.value,
                    iterations = 1,
                    modifier = Modifier.size(250.dp)
                )
            }
        }
    }
}