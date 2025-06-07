

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
import com.example.films_shop.main_screen.Genres.AuthorsGoogle
import com.example.films_shop.main_screen.Genres.authors
import com.example.films_shop.main_screen.Genres.genres
import com.example.films_shop.main_screen.Genres.saveSelectedAuthors
import com.example.films_shop.main_screen.objects.cold_start.GenreSelectionScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.BackGroundColorButtonLightGray
import com.example.films_shop.ui.theme.backColorChatCard
import com.example.films_shop.ui.theme.mainColorUiGreen
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun AuthorsBookSelectScreen(
    flag: Boolean = false,
    selectedAuthors:  SnapshotStateList<AuthorsGoogle>,
    mainScreenDataObject: MainScreenDataObject,
    navController: NavController,
    navData: GenreSelectionScreenDataObject,
    db: FirebaseFirestore,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val topAuthors = authors.take(genres.size / 2)
    val bottomAuthors = authors.drop(genres.size / 2)

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 16.dp)
        ) {
            Image(
                painter = if (flag) painterResource(id = R.drawable.rewelcome_3) else painterResource(id = R.drawable.welcome_3), // замените на своё изображение
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
            RowAuthors(authors = topAuthors, selectedAuthors = selectedAuthors)
            Spacer(modifier = Modifier.height(12.dp))
            RowAuthors(authors = bottomAuthors, selectedAuthors = selectedAuthors)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                Text(
                    text = "Выберите ваших любимых авторов книг",
                    color = if (isDark) Color.White else Color.Black,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "От 1 до 6 авторов",
                    color = Color.Gray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = {
                        saveSelectedAuthors(db, navData.uid, selectedAuthors)
                        onContinue()
//                  navController.navigate(mainScreenDataObject)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) backColorChatCard.copy(alpha = 0.5f) else BackGroundColorButtonLightGray
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
                if (selectedAuthors.isEmpty()) {
                    Toast.makeText(context, "Выберите хотя бы одного автора", Toast.LENGTH_SHORT).show()
                } else {
                    saveSelectedAuthors(db, navData.uid, selectedAuthors)
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
fun RowAuthors(
    authors: List<AuthorsGoogle>,
    selectedAuthors: SnapshotStateList<AuthorsGoogle>
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(authors) { author ->
            val isSelected = selectedAuthors.any { it.name.equals(author.name, ignoreCase = true) }

            Box(
                modifier = Modifier
                    .size(width = 160.dp, height = 220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (isSelected) {
                            selectedAuthors.removeIf { it.name.equals(author.name, ignoreCase = true) }
                        } else if (selectedAuthors.size < 6) {
                            selectedAuthors.add(author)
                        }
                    }
            ) {
                Image(
                    painter = painterResource(id = author.imageResId),
                    contentDescription = author.name,
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

                Text(
                    text = author.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                )
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
