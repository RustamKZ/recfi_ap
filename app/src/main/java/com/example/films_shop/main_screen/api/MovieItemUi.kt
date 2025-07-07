package com.example.films_shop.main_screen.api

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.films_shop.ui.theme.mainColorUiGreen

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
            val rating = movie.rating?.kp ?: movie.rating?.imdb ?:0.0
            if (rating != 0.0) {
                val backgroundColor = when {
                    rating > 7 -> mainColorUiGreen
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
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = movie.name ?: "Без названия",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}