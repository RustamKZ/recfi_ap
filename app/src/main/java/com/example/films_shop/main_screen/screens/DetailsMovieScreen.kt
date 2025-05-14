package com.example.films_shop.main_screen.screens

import MovieViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.films_shop.main_screen.api.ExternalId
import com.example.films_shop.main_screen.api.Genre
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.Persons
import com.example.films_shop.main_screen.api.Poster
import com.example.films_shop.main_screen.api.Rating
import com.example.films_shop.main_screen.api.recomendations.RecommendationViewModel
import com.example.films_shop.main_screen.business_logic.onFavsMovies
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MovieScreenDataObject
import com.example.films_shop.ui.theme.ButtonColor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.window.Dialog
import com.example.films_shop.main_screen.api.Backdrop
import com.example.films_shop.main_screen.business_logic.onBookmarkMovies
import com.example.films_shop.main_screen.business_logic.onRatedMovies
import com.example.films_shop.main_screen.screens.custom_ui.CutCornerLeftShape


@Composable
fun DetailsMovieScreen(
    navObject: DetailsNavMovieObject = DetailsNavMovieObject(),
    navData: MovieScreenDataObject? = null,
    movieViewModel: MovieViewModel,
    recViewModel: RecommendationViewModel,
    navController: NavController,
) {
    var expanded by remember { mutableStateOf(false) }
    val isFavorite = remember(
        movieViewModel.favoriteMoviesState.value,
        movieViewModel.favoriteTvSeriesState.value,
        movieViewModel.favoriteCartoonsState.value
    ) {
        movieViewModel.isInFavorites(navObject.id)
    }
    val isBookmark = remember(
        movieViewModel.bookmarkMoviesState.value,
        movieViewModel.bookmarkTvSeriesState.value,
        movieViewModel.bookmarkCartoonsState.value
    ) {
        movieViewModel.isInBookmarks(navObject.id)
    }
    val isRated = remember(
        movieViewModel.ratedMoviesState.value,
        movieViewModel.ratedTvSeriesState.value,
        movieViewModel.ratedCartoonsState.value
    ) {
        movieViewModel.isInRated(navObject.id)
    }
    val scrollState = rememberScrollState()
    val db = Firebase.firestore
    val recommendationMovies by recViewModel.recommendationMovies
    val isLoading by recViewModel.isLoading
    val error by recViewModel.error
    val id = navObject.tmdbId
    var expandedButton by remember { mutableStateOf(false) }
    LaunchedEffect(id)
    {
        if (id != 0) {
            when (navObject.type) {
                "movie" -> recViewModel.fetchRecommendations(id, navObject.type)
                "tv-series" -> recViewModel.fetchRecommendations(id, navObject.type)
                "cartoon" -> recViewModel.fetchRecommendations(id, navObject.type)
            }
        }
    }

    // ui stars
    val filledStars = (navObject.rating / 2).toInt()
    val hasHalfStar = (navObject.rating / 2) - filledStars >= 0.5
    val emptyStars = 5 - filledStars - if (hasHalfStar) 1 else 0
    // ui stars

    // ui userRating
    var showRatingDialog by remember { mutableStateOf(false) }
    var userRating by remember { mutableStateOf(5f) } // Ползунок будет от 1 до 10
    // ui userRating
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = LocalContentColor.current,
        topBar = {

        },
        bottomBar = {},

        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Правая часть с основными кнопками
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    // Колонка с раскрывающимися кнопками и основной кнопкой разворачивания
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier.width(150.dp)
                    ) {
                        // Выпадающие кнопки сверху с анимацией
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 62.dp)
                                .alpha(if (expandedButton) 1f else 0f)
                                .animateContentSize()
                        ) {
                            // Первая раскрывающаяся кнопка
                            if (expandedButton) {
                                FloatingActionButton(
                                    onClick = { showRatingDialog = true },
                                    containerColor = Color.White,
                                    contentColor = Color.Black,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.width(160.dp), // Фиксированная ширина
                                    elevation = FloatingActionButtonDefaults.elevation(
                                        defaultElevation = 0.dp,
                                        pressedElevation = 0.dp,
                                        focusedElevation = 0.dp,
                                        hoveredElevation = 0.dp
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (!isRated) Icons.Default.StarOutline else Icons.Default.Star,
                                            contentDescription = "Оценить"
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Оценить",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = custom_font
                                        )
                                    }
                                }

                                // Вторая раскрывающаяся кнопка
                                FloatingActionButton(
                                    onClick = {
                                        navData?.let { data ->
                                            val movie = Movie(
                                                id = navObject.id,
                                                externalId = ExternalId(tmdb = navObject.tmdbId),
                                                name = navObject.title,
                                                type = navObject.type,
                                                description = navObject.description,
                                                poster = Poster(url = navObject.imageUrl),
                                                backdrop = Backdrop(url = navObject.backdropUrl),
                                                genres = navObject.genre.split(", ")
                                                    .map { Genre(name = it) },
                                                year = navObject.year.toIntOrNull().toString(),
                                                persons = navObject.persons.split(", ")
                                                    .map { Persons(name = it) },
                                                rating = Rating(navObject.rating),
                                                isFavorite = isFavorite,
                                                isBookMark = isBookmark,
                                                isRated = isRated,
                                                userRating = navObject.userRating
                                            )
                                            onFavsMovies(db, data.uid, movie, !movie.isFavorite)
                                        }
                                    },
                                    containerColor = Color.White,
                                    contentColor = Color.Black,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.width(160.dp), // Фиксированная ширина
                                    elevation = FloatingActionButtonDefaults.elevation(
                                        defaultElevation = 0.dp,
                                        pressedElevation = 0.dp,
                                        focusedElevation = 0.dp,
                                        hoveredElevation = 0.dp
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (!isFavorite) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                                            contentDescription = "В избранное"
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "В избранное",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = custom_font
                                        )
                                    }
                                }
                            }
                        }

                        // Раскрывающаяся кнопка, всегда в той же позиции
                        FloatingActionButton(
                            onClick = { expandedButton = !expandedButton },
                            containerColor = if (expandedButton) Color.Gray else Color.White,
                            contentColor = Color.Black,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            Icon(
                                imageVector = if (!expandedButton) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }
                    }

                    // Отступ между кнопками
                    //Spacer(modifier = Modifier.width(70.dp))

                    // Правая большая кнопка "Посмотреть позже"
                    FloatingActionButton(
                        onClick = {
                            navData?.let { data ->
                                val movie = Movie(
                                    id = navObject.id,
                                    externalId = ExternalId(tmdb = navObject.tmdbId),
                                    name = navObject.title,
                                    type = navObject.type,
                                    description = navObject.description,
                                    poster = Poster(url = navObject.imageUrl),
                                    backdrop = Backdrop(url = navObject.backdropUrl),
                                    genres = navObject.genre.split(", ").map { Genre(name = it) },
                                    year = navObject.year.toIntOrNull().toString(),
                                    persons = navObject.persons.split(", ")
                                        .map { Persons(name = it) },
                                    rating = Rating(navObject.rating),
                                    isFavorite = isFavorite,
                                    isBookMark = isBookmark,
                                    isRated = isRated,
                                    userRating = navObject.userRating
                                )
                                onBookmarkMovies(db, data.uid, movie, !movie.isBookMark)
                            }
                        },
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = if (!isBookmark) Icons.Default.BookmarkBorder else Icons.Default.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                            Text(
                                text = "Посмотреть позже",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = custom_font,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(innerPadding),
        )
        {
            if (showRatingDialog) {
                Dialog(onDismissRequest = { showRatingDialog = false }) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .wrapContentHeight()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Оцените фильм",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Slider(
                                value = userRating,
                                onValueChange = { userRating = it },
                                valueRange = 1f..10f,
                                steps = 8, // чтобы по 1 шагу (10-1)/9 = 1
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFE16B04),
                                    activeTrackColor = Color(0xFFE16B04)
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Вы выбрали: ${userRating.toInt()}",
                                fontSize = 16.sp,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    navData?.let { data ->
                                        val movie = Movie(
                                            id = navObject.id,
                                            externalId = ExternalId(tmdb = navObject.tmdbId),
                                            name = navObject.title,
                                            type = navObject.type,
                                            description = navObject.description,
                                            poster = Poster(url = navObject.imageUrl),
                                            backdrop = Backdrop(url = navObject.backdropUrl),
                                            genres = navObject.genre.split(", ")
                                                .map { Genre(name = it) },
                                            year = navObject.year.toIntOrNull().toString(),
                                            persons = navObject.persons.split(", ")
                                                .map { Persons(name = it) },
                                            rating = Rating(navObject.rating),
                                            isFavorite = isFavorite,
                                            isBookMark = isBookmark,
                                            isRated = isRated,
                                            userRating = userRating.toInt() // <-- Ставим выбранный рейтинг
                                        )
                                        onRatedMovies(db, data.uid, movie)
                                    }
                                    showRatingDialog = false // Закрыть диалог
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE16B04),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Поставить оценку",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            )
            {
                Box {
                    AsyncImage(
                        model = navObject.backdropUrl,
                        contentDescription = "Постер фильма",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .offset(y = (-50).dp)
                        .padding(start = 16.dp, end = 16.dp),
                    //verticalAlignment = Alignment.Bottom
                )
                {
                    AsyncImage(
                        model = navObject.imageUrl,
                        contentDescription = "Постер фильма",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(190.dp)
                            .height(280.dp)
                            .clip(RoundedCornerShape(12.dp)),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.padding(top = 70.dp)
                        //modifier = Modifier
                        //.offset(y = (40).dp)
                    )
                    {
                        Text(
                            text = navObject.title,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            //fontFamily = custom_font
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row() {
                            repeat(filledStars) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFE16B04)
                                )
                            }
                            if (hasHalfStar) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.StarHalf,
                                    contentDescription = null,
                                    tint = Color(0xFFE16B04)
                                )
                            }
                            repeat(emptyStars) {
                                Icon(
                                    imageVector = Icons.Default.StarOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFE16B04)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row()
                        {
                            Text(
                                text = navObject.year,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = custom_font
                            )
                            Surface(
                                shape = CutCornerLeftShape(cutSize = 20.dp),
                                color = Color.Transparent,
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = Modifier
                                    .height(24.dp)
                                    .wrapContentWidth()
                                    .padding(start = 4.dp)
                            ) {
                                Box(modifier = Modifier.padding(start = 10.dp, bottom = 4.dp)) {
                                    Text(
                                        text = String.format("%.1f", navObject.rating),
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = navObject.genre,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = custom_font
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Режиссер: ",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = custom_font
                        )
                        Text(
                            text = navObject.persons,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = custom_font
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-50).dp)
                        .padding(start = 16.dp, end = 16.dp),
                )
                {
                    Text(
                        text = "Описание",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = custom_font,
                        textAlign = TextAlign.Left
                    )
                    Text(
                        text = navObject.description,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontFamily = custom_font,
                        maxLines = if (expanded) Int.MAX_VALUE else 4, // Ограничение строк
                        overflow = TextOverflow.Ellipsis
                    )

                    // Кнопка "Читать далее"
                    TextButton(onClick = { expanded = !expanded }) {
                        Text(
                            text = if (expanded) "Скрыть" else "Читать далее",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                    when (navObject.type) {
                        "movie" -> Text(
                            text = "Похожие фильмы",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        "tv-series" -> Text(
                            text = "Похожие сериалы",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        "cartoon" -> Text(
                            text = "Похожие мультфильмы",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isLoading) {
                            // Показать индикатор загрузки
                            items(3) {
                                Box(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(220.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.height(30.dp),
                                        color = ButtonColor
                                    )
                                }
                            }
                        } else if (recommendationMovies.isNotEmpty()) {
                            items(recommendationMovies.size) { index ->
                                val movie = recommendationMovies[index]
                                Box(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .width(120.dp)
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(Color.Gray)
                                        .clickable {
                                            // Навигация к деталям рекомендуемого фильма
                                            navData?.let { data ->
                                                val detailsNavObject = DetailsNavMovieObject(
                                                    id = movie.id,
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
                                                    rating = movie.rating?.kp ?: 0.0,
                                                    persons = movie.persons?.joinToString(", ") { it.name }
                                                        ?: "Неизвестно",
                                                    isFavorite = movieViewModel.isInFavorites(
                                                        movie.id
                                                    ),
                                                    isBookMark = movieViewModel.isInBookmarks(
                                                        movie.id
                                                    ),
                                                    isRated = movieViewModel.isInRated(
                                                        movie.id
                                                    ),
                                                    userRating = movie.userRating ?: 0
                                                )
                                                navController.navigate(detailsNavObject)
                                            }
                                        },
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
                        } else if (error != null) {
                            // Показать сообщение об ошибке
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Не удалось загрузить рекомендации",
                                        color = Color.Red,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        } else {
                            // Показать сообщение, если рекомендаций нет
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Нет рекомендаций для этого фильма",
                                        color = Color.Gray,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}