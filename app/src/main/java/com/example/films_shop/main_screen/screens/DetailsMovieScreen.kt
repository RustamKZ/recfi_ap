package com.example.films_shop.main_screen.screens

import MovieViewModel
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import coil.compose.AsyncImage
import com.example.films_shop.main_screen.api.Genre
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.Persons
import com.example.films_shop.main_screen.api.Poster
import com.example.films_shop.main_screen.api.Rating
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
    movieViewModel: MovieViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val favoriteMovies = movieViewModel.favoriteMoviesState.value
    val isFavorite = favoriteMovies.any { it.id == navObject.id }
    val scrollState = rememberScrollState()
    val db = Firebase.firestore
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