package com.example.films_shop.main_screen.screens.cold_start

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.films_shop.R
import com.example.films_shop.main_screen.Genres.GenreKP
import com.example.films_shop.main_screen.Genres.genres
import com.example.films_shop.main_screen.Genres.saveSelectedGenres
import com.example.films_shop.main_screen.objects.cold_start.GenreSelectionScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.BackGroundColorButtonLightGray
import com.example.films_shop.ui.theme.backColorChatCard
import com.example.films_shop.ui.theme.mainColorUiGreen
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun GenreSelectionScreen(
    flag: Boolean = false,
    selectedGenres:  SnapshotStateList<GenreKP>,
    mainScreenDataObject: MainScreenDataObject,
    navController: NavController,
    navData: GenreSelectionScreenDataObject,
    db: FirebaseFirestore,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val topGenres = genres.take(genres.size / 2)
    val bottomGenres = genres.drop(genres.size / 2)

    Box(modifier = Modifier.fillMaxSize()) {
        // Фоновое изображение
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 16.dp)
        ) {
            Image(
                painter = if (flag) painterResource(id = R.drawable.rewelcome_2) else painterResource(
                    id = R.drawable.welcome_2
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Затемнение или осветление
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isDark) {
                            listOf(BackGroundColor.copy(alpha = 1f), Color.Transparent, BackGroundColor.copy(alpha = 1f))
                        } else {
                            listOf(Color.White.copy(alpha = 1f), Color.Transparent, Color.White.copy(alpha = 1f))
                        }
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // Без паддинга
            GenreRow(genres = topGenres, selectedGenres = selectedGenres)
            Spacer(modifier = Modifier.height(12.dp))
            GenreRow(genres = bottomGenres, selectedGenres = selectedGenres)

            // Оборачиваем только текст и кнопку в отдельную колонку с паддингом
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.height(100.dp))
                Text(
                    text = "Выберите ваши любимые жанры",
                    color = if (isDark) Color.White else Color.Black,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "От 1 до 6 жанров",
                    color = Color.Gray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Button(
                    onClick = {
                        saveSelectedGenres(db, navData.uid, selectedGenres)
                        onContinue()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) backColorChatCard.copy(alpha = 0.5f)
                        else BackGroundColorButtonLightGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .padding(top = 16.dp)
                ) {
                    Text(
                        "Далее",
                        fontSize = 30.sp,
                        color = mainColorUiGreen
                    )
                }
            }
        }

        Button(
            onClick = {
                if (selectedGenres.isEmpty()) {
                    Toast.makeText(context, "Выберите хотя бы один жанр", Toast.LENGTH_SHORT).show()
                } else {
                    saveSelectedGenres(db, navData.uid, selectedGenres)
                    onContinue()
//                    navController.navigate(mainScreenDataObject)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) backColorChatCard.copy(alpha = 0.5f) else BackGroundColorButtonLightGray
            ),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 20.dp)
        ) {
            Text(
                text = "Пропустить",
                fontSize = 20.sp,
                color = mainColorUiGreen
            )
        }
//        Text(
//            text = "Пропустить",
//            fontSize = 20.sp,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(top = 32.dp, end = 20.dp)
//                .clickable {
//                    if (selectedGenres.isEmpty()) {
//                        Toast.makeText(context, "Выберите хотя бы один жанр", Toast.LENGTH_SHORT).show()
//                    } else {
//                        saveSelectedGenres(db, navData.uid, selectedGenres)
//                        navController.navigate(mainScreenDataObject)
//                    }
//                },
//            color = mainColorUiGreen,
//            fontWeight = FontWeight.Bold
//        )
    }
}

@Composable
fun GenreRow(
    genres: List<GenreKP>,
    selectedGenres: SnapshotStateList<GenreKP>
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(genres) { genre ->
            val isSelected = selectedGenres.any { it.name.equals(genre.name, ignoreCase = true) }

            Box(
                modifier = Modifier
                    .size(width = 160.dp, height = 220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (isSelected) {
                            selectedGenres.removeIf { it.name.equals(genre.name, ignoreCase = true) }
                        } else if (selectedGenres.size < 6) {
                            selectedGenres.add(genre)
                        }
                    }
            ) {
                // Фоновое изображение
                Image(
                    painter = painterResource(id = genre.imageResId),
                    contentDescription = genre.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Затемнение (если НЕ выбрано)
                if (!isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)) // любое нужное затемнение
                    )
                }

                // Название жанра
                Text(
                    text = genre.name.replaceFirstChar { it.uppercase() },
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                )

                // Иконка поверх всего, всегда яркая
                Icon(
                    imageVector = if (!isSelected) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp),
                    tint = mainColorUiGreen,
                    contentDescription = "В избранное"
                )
            }
        }
    }
}
