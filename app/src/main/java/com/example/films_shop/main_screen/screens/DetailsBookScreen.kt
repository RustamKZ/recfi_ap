package com.example.films_shop.main_screen.screens

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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.films_shop.main_screen.api.Backdrop
import com.example.films_shop.main_screen.api.BookApi.Book
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.api.ExternalId
import com.example.films_shop.main_screen.api.Genre
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.Persons
import com.example.films_shop.main_screen.api.Poster
import com.example.films_shop.main_screen.api.Rating
import com.example.films_shop.main_screen.api.recomendations.RecommendationViewModel
import com.example.films_shop.main_screen.business_logic.onFavsBooks
import com.example.films_shop.main_screen.business_logic.onFavsMovies
import com.example.films_shop.main_screen.objects.BookScreenDataObject
import com.example.films_shop.main_screen.objects.DetailsNavBookObject
import com.example.films_shop.main_screen.objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.screens.custom_ui.CutCornerLeftShape
import com.example.films_shop.ui.theme.ButtonColor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun DetailsBookScreen(
    navObject: DetailsNavBookObject = DetailsNavBookObject(),
    navData: BookScreenDataObject? = null,
    bookViewModel: BookViewModel,
    recViewModel: RecommendationViewModel,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }
    val favoriteBooks = bookViewModel.favoriteBooksState.value
    val isFavorite = favoriteBooks.any { it.id == navObject.id }
    val scrollState = rememberScrollState()
    val db = Firebase.firestore

    val recommendationBooks by recViewModel.recommendationBooks
    val isLoading by recViewModel.isLoading
    val error by recViewModel.error
    val id = navObject.isbn10
    LaunchedEffect(id)
    {
        recViewModel.fetchRecommendationsBooks(id, navObject.authors)
    }
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = LocalContentColor.current,
        topBar = {

        },
        bottomBar = {},
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navData?.let { data ->
                        val book = Book(
                            id = navObject.id,
                            isbn10 = navObject.isbn10,
                            title = navObject.title,
                            description = navObject.description,
                            thumbnail = navObject.thumbnail,
                            authors = navObject.authors.split(", "),
                            publishedDate = navObject.publishedDate,
                            isFavorite = isFavorite
                        )
                        onFavsBooks(db, data.uid, book, !book.isFavorite)
                    }
                },
                contentColor = Color.Black,
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = if (!isFavorite) "Добавить в избранное" else "Удалить из избранных",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = custom_font,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            )
            {
                Box {
                    AsyncImage(
                        model = navObject.thumbnail.replace("http://", "https://"),
                        contentDescription = "Обложка книги",
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
                        model = navObject.thumbnail.replace("http://", "https://"),
                        contentDescription = "Обложка книги",
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
                        Row()
                        {
                            Text(
                                text = navObject.publishedDate,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = custom_font
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Авторы: ",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = custom_font
                        )
                        Text(
                            text = navObject.authors,
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

                        Text(
                            text = "Похожие книги",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                        } else if (recommendationBooks.isNotEmpty()) {
                            items(recommendationBooks.size) { index ->
                                val book = recommendationBooks[index]
                                Box(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .width(120.dp)
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(Color.Gray)
                                        .clickable {
                                            // Навигация к деталям рекомендуемого фильма
                                            navData.let { data ->
                                                val detailsNavBookObject = DetailsNavBookObject(
                                                    id = book.id,
                                                    isbn10 = book.isbn10,
                                                    title = book.title,
                                                    authors = book.authors?.joinToString(", ")
                                                        ?: "Неизвестно",
                                                    description = book.description ?: "Описание отсутствует",
                                                    thumbnail = book.thumbnail?: "",
                                                    publishedDate = book.publishedDate ?: "Неизвестно",
                                                    isFavorite = book.isFavorite
                                                )
                                                navController.navigate(detailsNavBookObject)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = book.thumbnail?.replace("http://", "https://"),
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