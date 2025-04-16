package com.example.films_shop.main_screen.screens

import MovieViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.films_shop.main_screen.objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.objects.MovieScreenDataObject
import com.example.films_shop.ui.theme.ButtonColor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun DetailsMovieScreen(
    navObject: DetailsNavMovieObject = DetailsNavMovieObject(),
    navData: MovieScreenDataObject? = null,
    movieViewModel: MovieViewModel,
    recViewModel: RecommendationViewModel,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }
    val isFavorite = remember(
        movieViewModel.favoriteMoviesState.value,
        movieViewModel.favoriteTvSeriesState.value,
        movieViewModel.favoriteCartoonsState.value
    ) {
        movieViewModel.isInFavorites(navObject.id)
    }
    val scrollState = rememberScrollState()
    val db = Firebase.firestore
    val recommendationMovies by recViewModel.recommendationMovies
    val isLoading by recViewModel.isLoading
    val error by recViewModel.error
    val id = navObject.tmdbId
    if (id != 0) {
        recViewModel.fetchRecommendations(id)
    }
    Scaffold(
        topBar = {

        },
        bottomBar = {

        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Column()
                {
                    AsyncImage(
                        model = navObject.imageUrl,
                        contentDescription = "Постер фильма",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.FillHeight
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Text(
                            text = navObject.title,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            fontFamily = custom_font
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Жанр: ",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = custom_font
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = navObject.genre,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = custom_font
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Год: ",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = custom_font
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = navObject.year,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = custom_font
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
                                color = Color.Blue,
                                fontSize = 16.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Похожие фильмы",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                        }
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isLoading) {
                                // Показать индикатор загрузки
                                items(3) {
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .width(120.dp)
                                            .height(200.dp)
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
                                                        genre = movie.genres?.joinToString(", ") { it.name } ?: "Неизвестно",
                                                        year = movie.year ?: "Неизвестно",
                                                        description = movie.description ?: "Описание отсутствует",
                                                        imageUrl = movie.poster?.url ?: "",
                                                        rating = movie.rating?.kp ?: 0.0,
                                                        persons = movie.persons?.joinToString(", ") { it.name } ?: "Неизвестно",
                                                        isFavorite = movieViewModel.isInFavorites(movie.id)
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
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom, // Размещаем кнопку внизу
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Button(
                            onClick = {
                                // Используем переданный navData для получения uid пользователя
                                // и добавления фильма в избранное
                                navData?.let { data ->
                                    // Здесь нужно создать объект Movie из navObject
                                    val movie = Movie(
                                        id = navObject.id,
                                        externalId = ExternalId(tmdb = navObject.tmdbId),
                                        name = navObject.title,
                                        type = navObject.type,
                                        description = navObject.description,
                                        poster = Poster(url = navObject.imageUrl),
                                        genres = navObject.genre.split(", ")
                                            .map { Genre(name = it) },
                                        year = navObject.year.toIntOrNull().toString(),
                                        persons = navObject.persons.split(", ").map { Persons(name = it) },
                                        rating = Rating(navObject.rating),
                                        isFavorite = isFavorite
                                    )
                                    // Вызываем функцию onFavsMovies
                                    onFavsMovies(db, data.uid, movie, !movie.isFavorite)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(bottom = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                        )
                        {
                            Text(
                                text = if (!isFavorite) "Добавить в избранное" else "Удалить из избранных",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = custom_font,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}