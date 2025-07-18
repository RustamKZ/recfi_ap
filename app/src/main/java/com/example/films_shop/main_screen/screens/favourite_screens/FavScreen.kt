package com.example.films_shop.main_screen.screens.favourite_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.bottom_menu.MainViewModel
import com.example.films_shop.main_screen.objects.fav_screens_objects.FavBookScreenDataObject
import com.example.films_shop.main_screen.objects.fav_screens_objects.FavCartoonScreenDataObject
import com.example.films_shop.main_screen.objects.fav_screens_objects.FavMovieScreenDataObject
import com.example.films_shop.main_screen.objects.fav_screens_objects.FavSeriesScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.main_screen.screens.font_books_rus
import com.example.films_shop.main_screen.screens.font_cartoon_rus
import com.example.films_shop.main_screen.screens.font_films_rus
import com.example.films_shop.main_screen.screens.font_series_rus
import com.example.films_shop.ui.theme.mainColorUiGreen

@Composable
fun FavScreen(
    navData: MainScreenDataObject,
    navController: NavController,
    viewModel: MainViewModel
) {
    val isDark = isSystemInDarkTheme()
    val backColor = if (isDark) Color.Black else Color.White
    val textColor = if (isDark) mainColorUiGreen else mainColorUiGreen
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
        },
        bottomBar = {
            BottomMenu(
                navController = navController,
                uid = navData.uid,
                email = navData.email,
                selectedTab = viewModel.selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(backColor)
                    .clickable {
                        navController.navigate(
                            FavMovieScreenDataObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Text(
                    text = "ФИЛЬМЫ",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font_films_rus,
                    fontSize = 70.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(backColor)
                    .clickable {
                        navController.navigate(
                            FavCartoonScreenDataObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Text(
                    text = "МУЛЬТФИЛЬМЫ",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font_cartoon_rus,
                    fontSize = 60.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(backColor)
                    .clickable {
                        navController.navigate(
                            FavSeriesScreenDataObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Text(
                    text = "Сериалы",
                    color = textColor,
                    fontFamily = font_series_rus,
                    fontWeight = FontWeight.Bold,
                    fontSize = 70.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(backColor)
                    .clickable {
                        navController.navigate(
                            FavBookScreenDataObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Text(
                    text = "Книги",
                    color = textColor,
                    fontFamily = font_books_rus,
                    fontWeight = FontWeight.Bold,
                    fontSize = 60.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }
    }
}