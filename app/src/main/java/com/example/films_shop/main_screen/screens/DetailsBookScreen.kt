package com.example.films_shop.main_screen.screens

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
import com.example.films_shop.main_screen.api.BookApi.Book
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.business_logic.onFavsBooks
import com.example.films_shop.main_screen.objects.BookScreenDataObject
import com.example.films_shop.main_screen.objects.DetailsNavBookObject
import com.example.films_shop.ui.theme.ButtonColor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun DetailsBookScreen(
    navObject: DetailsNavBookObject = DetailsNavBookObject(),
    navData: BookScreenDataObject? = null,
    bookViewModel: BookViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val favoriteBooks = bookViewModel.favoriteBooksState.value
    val isFavorite = favoriteBooks.any { it.id == navObject.id }
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
                        model = navObject.thumbnail.replace("http://", "https://"),
                        contentDescription = "Обложка книги",
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
                            text = "Авторы: ",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = custom_font
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = navObject.authors,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = custom_font
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Дата публикации: ",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = custom_font
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = navObject.publishedDate,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = custom_font
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
                                navData?.let { data ->
                                    val book = Book(
                                        id = navObject.id,
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