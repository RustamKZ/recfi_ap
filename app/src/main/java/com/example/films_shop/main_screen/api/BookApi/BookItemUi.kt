package com.example.films_shop.main_screen.api.BookApi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun BookItemUi(
    book: Book,
    onBookDetailsClick: (Book) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                onBookDetailsClick(book)
            }
    ) {
        AsyncImage(
            model = book.thumbnail?.replace("http://", "https://"),
            contentDescription = "Обложка книги",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(15.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = book.title ?: "Без названия",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}