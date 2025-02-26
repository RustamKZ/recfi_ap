package com.example.films_shop.main_screen.api

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun MovieitemUi(
    movie: Movie,
    onMovieDetailsClick: (Movie) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                onMovieDetailsClick(movie)
            }
    ) {
        AsyncImage(
            model = movie.poster?.url,
            contentDescription = "Постер фильма",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Имя: ${movie.name ?: "Неизвестно"}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "Год: ${movie.year ?: "Неизвестно"}",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Text(
            text = "Рейтинг: ${movie.rating?.kp ?: 0.0}",
            fontSize = 14.sp,
            color = Color.Blue
        )
    }
}