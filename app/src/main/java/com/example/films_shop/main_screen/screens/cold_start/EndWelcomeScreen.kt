package com.example.films_shop.main_screen.screens.cold_start

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.films_shop.R
import com.example.films_shop.main_screen.Genres.AuthorsGoogle
import com.example.films_shop.main_screen.Genres.GenreKP
import com.example.films_shop.main_screen.Genres.saveSelectedAuthors
import com.example.films_shop.main_screen.Genres.saveSelectedGenres
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.backColorChatCard
import com.example.films_shop.ui.theme.mainColorUiGreen
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EndWelcomeScreen(
    flag: Boolean = false,
    db: FirebaseFirestore,
    selectedGenres: SnapshotStateList<GenreKP>,
    selectedAuthors:  SnapshotStateList<AuthorsGoogle>,
    navController: NavController,
    mainScreenDataObject: MainScreenDataObject
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = if (flag) painterResource(id = R.drawable.rewelcome_4) else painterResource(id = R.drawable.welcome_4), // замените на своё изображение
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
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
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Спасибо!",
                color = if (isDark) Color.White else Color.Black,
                style = MaterialTheme.typography.h5.copy(color = Color.White),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ты всегда можешь изменить свой выбор на странице своего аккаунта",
                color = if (isDark) Color.White else Color.Black,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (selectedGenres.isEmpty()) {
                        Toast.makeText(context, "Выберите хотя бы один жанр", Toast.LENGTH_SHORT).show()
                    }
                    else if (selectedAuthors.isEmpty()) {
                        Toast.makeText(context, "Выберите хотя бы одного автора", Toast.LENGTH_SHORT).show()
                    }else {
                        saveSelectedGenres(db, mainScreenDataObject.uid, selectedGenres)
                        saveSelectedAuthors(db, mainScreenDataObject.uid, selectedAuthors)
                        navController.navigate(mainScreenDataObject)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = backColorChatCard.copy(alpha = 0.5f)
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
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
