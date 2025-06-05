package com.example.films_shop.main_screen.screens

import MovieViewModel
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Divider
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.request.ImageRequest
import com.example.films_shop.R
import com.example.films_shop.main_screen.api.Backdrop
import com.example.films_shop.main_screen.api.Votes
import com.example.films_shop.main_screen.business_logic.onBookmarkMovies
import com.example.films_shop.main_screen.business_logic.onRatedMovies
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.BackGroundColorButton
import com.example.films_shop.ui.theme.BackGroundColorButtonLightGray
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@Composable
fun ShimmerImageItem(
    imageUrl: String,
    onClick: (String) -> Unit, // Добавляем колбэк для обработки клика
) {
    var isImageLoading by remember { mutableStateOf(true) }

    Box( // Используем Box, чтобы позволить размещать clickable поверх, хотя Modifier.clickable работает и напрямую на AsyncImage
        modifier = Modifier
            .padding(end = 12.dp)
            .height(200.dp)
            .width(350.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(imageUrl) } // Делаем весь элемент кликабельным
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .listener(
                    onSuccess = { _, _ -> isImageLoading = false },
                    onError = { _, _ -> isImageLoading = false }
                )
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize() // Делаем AsyncImage заполняющим Box
                .placeholder(
                    visible = isImageLoading,
                    color = Color.LightGray,
                    highlight = PlaceholderHighlight.shimmer(
                        highlightColor = Color.White.copy(alpha = 0.6f)
                    )
                )
        )
    }
}

@Composable
fun RatingCard(
    navObject: DetailsNavMovieObject,
    isDark: Boolean,
    imageGradColor: Color,
    buttonBackgroundColor: Color,
    buttonTextColor: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = buttonBackgroundColor,
                    shape = RoundedCornerShape(16.dp)
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val itemModifier = Modifier
                .weight(1f)
            //.padding(vertical = 16.dp)
            val formattedRatingKp = String.format("%.1f", navObject.ratingKp)
            val formattedRatingImdb = String.format("%.1f", navObject.ratingImdb)
            RatingItem(
                title = "Кинопоиск",
                rating = formattedRatingKp,
                votes = navObject.votesKp.toString(),
                buttonTextColor,
                itemModifier
            )
            Divider(
                color = Color.LightGray,
                modifier = Modifier
                    .height(150.dp)
                    .width(1.dp)
            )
            RatingItem(
                title = "IMDb",
                rating = formattedRatingImdb,
                votes = navObject.votesImdb.toString(),
                buttonTextColor,
                itemModifier
            )
        }
    }
}

@Composable
fun RatingItem(
    title: String,
    rating: String,
    votes: String,
    buttonTextColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = buttonTextColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = rating,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold,
                color = buttonTextColor,
                fontSize = 35.sp,
            ),
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = votes,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            fontSize = 15.sp,
        )
    }
}

@Composable
fun CastAndCrewSection(navObject: DetailsNavMovieObject) {
    val castList = remember(navObject.persons) {
        navObject.persons.split(", ")
            .map {
                val (name, photo) = it.split("|", limit = 2)
                Cast(name = name, photoUrl = photo)
            }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(castList) { cast ->
                CastItem(cast)
            }
        }
    }
}


data class Cast(val name: String, val photoUrl: String)

@Composable
fun CastItem(cast: Cast) {
    Column(
        modifier = Modifier
            .padding(end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = cast.photoUrl,
            contentDescription = cast.name,
            error = painterResource(R.drawable.error_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.LightGray) // placeholder bg
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = cast.name,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TestDetailsMovieScreen(
    navObject: DetailsNavMovieObject = DetailsNavMovieObject(),
    navData: MovieScreenDataObject? = null,
    movieViewModel: MovieViewModel,
    recViewModel: RecommendationViewModel,
    navController: NavController,
) {
    val isDark = isSystemInDarkTheme()
    val imageGradColor = if (isDark) BackGroundColor else Color.White
    val textColor = if (isDark) Color.White else Color.Black
    val buttonBackgroundColor =
        if (isDark) BackGroundColorButton else BackGroundColorButtonLightGray
    val buttonTextColor = if (isDark) Color.White else Color.Black
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
        Log.d("TestNav", "${navObject.persons}")
        if (id != 0) {
            when (navObject.type) {
                "movie" -> recViewModel.fetchRecommendations(id, navObject.type)
                "tv-series" -> recViewModel.fetchRecommendations(id, navObject.type)
                "cartoon" -> recViewModel.fetchRecommendations(id, navObject.type)
            }
            movieViewModel.loadImagesForMovie(navObject.id.toInt())
        }
    }
    val images by remember { movieViewModel.imagesState }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    // ui stars
    val filledStars = (navObject.ratingKp / 2).toInt()
    val hasHalfStar = (navObject.ratingKp / 2) - filledStars >= 0.5
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
                                                    .map {
                                                        val parts = it.split("|")
                                                        val name = parts.getOrElse(0) { "" }
                                                        val photo = parts.getOrElse(1) { "" }
                                                        Persons(name = name, photo = photo)
                                                    },
                                                rating = Rating(
                                                    navObject.ratingKp,
                                                    navObject.ratingImdb
                                                ),
                                                votes = Votes(
                                                    navObject.votesKp,
                                                    navObject.votesImdb
                                                ),
                                                isFavorite = isFavorite,
                                                isBookMark = isBookmark,
                                                isRated = isRated,
                                                userRating = navObject.userRating
                                            )
                                            Log.d("TestNav", "${movie.persons}")
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
                                        .map {
                                            val parts = it.split("|")
                                            val name = parts.getOrElse(0) { "" }
                                            val photo = parts.getOrElse(1) { "" }
                                            Persons(name = name, photo = photo)
                                        },
                                    rating = Rating(navObject.ratingKp, navObject.ratingImdb),
                                    votes = Votes(navObject.votesKp, navObject.votesImdb),
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
                                                .map {
                                                    val parts = it.split("|")
                                                    val name = parts.getOrElse(0) { "" }
                                                    val photo = parts.getOrElse(1) { "" }
                                                    Persons(name = name, photo = photo)
                                                },
                                            rating = Rating(
                                                navObject.ratingKp,
                                                navObject.ratingImdb
                                            ),
                                            votes = Votes(navObject.votesKp, navObject.votesImdb),
                                            isFavorite = isFavorite,
                                            isBookMark = isBookmark,
                                            isRated = isRated,
                                            userRating = userRating.toInt() // <-- Ставим выбранный рейтинг
                                        )
                                        onRatedMovies(db, data.uid, movie)
                                        Log.d("TestNavData", "User: ${navData.email}")
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
                    .align(Alignment.Center)
            )
            {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                ) {
                    AsyncImage(
                        model = navObject.backdropUrl,
                        contentDescription = "Постер фильма",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Затемнение снизу выбранным цветом
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        imageGradColor
                                    ) // здесь можно указать любой цвет
                                )
                            )
                    )
                }


                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = navObject.title,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 70.sp,
                        modifier = Modifier.align(Alignment.Center),
                        fontFamily = test_font
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                val genresFormatted = navObject.genre.split(",")
                    .map { it.trim().replaceFirstChar { c -> c.uppercaseChar() } }
                    .take(3)
                    .joinToString("   ")
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = genresFormatted,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_font
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = navObject.year,
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_font
                        )

                        Text(
                            text = navObject.persons.split(", ")
                                .map { it.substringBefore("|") }.joinToString(),
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_font
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                    )
                    {
                        Text(
                            text = navObject.description,
                            color = textColor,
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
                        Text(
                            text = "Рейтинг",
                            fontSize = 25.sp,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        RatingCard(
                            navObject,
                            isDark,
                            imageGradColor,
                            buttonBackgroundColor,
                            buttonTextColor
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        Text(
                            text = "Cъёмочная группа",
                            fontSize = 25.sp,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        CastAndCrewSection(navObject)
                        Spacer(modifier = Modifier.height(25.dp))
                        if (images.isNotEmpty()) {
                            Text(
                                text = "Изображения",
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LazyRow() {
                                items(images) { image ->
                                    ShimmerImageItem(
                                        imageUrl = image.url,
                                        onClick = { url -> selectedImageUrl = url }
                                    )
                                }
                            }
                            selectedImageUrl?.let { imageUrl ->
                                Dialog(
                                    onDismissRequest = { selectedImageUrl = null },
                                    properties = DialogProperties(usePlatformDefaultWidth = false) // Отключаем стандартную ширину платформы
                                ) {
                                    // Состояния для зума и панорамирования
                                    var scale by remember { mutableStateOf(1f) }
                                    var offset by remember { mutableStateOf(Offset.Zero) }

                                    // Порог для закрытия диалога при панорамировании
                                    val dismissThreshold = 100.dp // Порог в dp

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.9f))
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                selectedImageUrl = null
                                            }
                                            .pointerInput(Unit) {
                                                detectTransformGestures(
                                                    onGesture = { centroid, pan, zoom, _ ->
                                                        scale = (scale * zoom).coerceIn(
                                                            0.5f,
                                                            5f
                                                        ) // Ограничиваем зум от 0.5x до 5x


                                                        if (scale > 1.05f) {
                                                            offset += pan
                                                        } else {
                                                            if (kotlin.math.abs(pan.y) > dismissThreshold.toPx()) {
                                                                selectedImageUrl =
                                                                    null
                                                            }
                                                            offset = Offset.Zero
                                                        }
                                                    }
                                                )
                                            }
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(imageUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = null,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .graphicsLayer(
                                                    scaleX = scale,
                                                    scaleY = scale,
                                                    translationX = offset.x,
                                                    translationY = offset.y
                                                )
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(25.dp))
                        when (navObject.type) {
                            "movie" -> Text(
                                text = "Похожие фильмы",
                                fontSize = 25.sp,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            "tv-series" -> Text(
                                text = "Похожие сериалы",
                                fontSize = 25.sp,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            "cartoon" -> Text(
                                text = "Похожие мультфильмы",
                                fontSize = 25.sp,
                                style = MaterialTheme.typography.headlineMedium,
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
                                            .width(170.dp)
                                            .height(250.dp)
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
                                                        ratingKp = movie.rating?.kp ?: 0.0,
                                                        ratingImdb = movie.rating?.imdb ?: 0.0,
                                                        votesKp = movie.votes?.kp ?: 0,
                                                        votesImdb = movie.votes?.imdb ?: 0,
                                                        persons = movie.persons?.joinToString(", ") { "${it.name}|${it.photo}" }
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
                                            val rating = when {
                                                movie.rating?.kp != null && movie.rating.kp > 0.0 -> movie.rating.kp
                                                movie.rating?.imdb != null && movie.rating.imdb > 0.0 -> movie.rating.imdb
                                                else -> 0.0
                                            }
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
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}