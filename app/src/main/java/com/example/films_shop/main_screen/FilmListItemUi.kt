package com.example.films_shop.main_screen

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.films_shop.R
import com.example.films_shop.main_screen.data.Film

@Composable
fun FilmListItemUi(
    showEditButton: Boolean = false,
    film: Film,
    onEditClick: (Film) -> Unit,
    onFavoriteClick: () -> Unit,
    onFilmDetailsClick: (Film) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onFilmDetailsClick(film)
            }
    ) {
        val base64Image = Base64.decode(film.imageUrl, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(
            base64Image, 0,
            base64Image.size
        )
        AsyncImage(
            model = bitmap,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(15.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            film.title,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            fontFamily = custom_font
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            film.description,
            color = Color.Gray,
            fontSize = 16.sp,
            fontFamily = custom_font,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.fillMaxWidth().weight(1f),
                text = film.year,
                color = Color.Blue,
                fontSize = 18.sp,
                fontFamily = custom_font,
                fontWeight = FontWeight.Bold,
            )
            if (showEditButton) IconButton(onClick = {
                onEditClick(film)
            }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Logo"
                )
            }
            IconButton(onClick = {
                onFavoriteClick()
            }) {
                Icon(
                    if (film.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite Logo"
                )
            }
        }
    }
}