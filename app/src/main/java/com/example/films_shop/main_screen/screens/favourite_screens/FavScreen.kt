package com.example.films_shop.main_screen.screens.favourite_screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.films_shop.R
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.objects.FavBookScreenDataObject
import com.example.films_shop.main_screen.objects.FavCartoonScreenDataObject
import com.example.films_shop.main_screen.objects.FavMovieScreenDataObject
import com.example.films_shop.main_screen.objects.FavSeriesScreenDataObject
import com.example.films_shop.main_screen.objects.MainScreenDataObject

@Composable
fun FavScreen(
    navData: MainScreenDataObject,
    navController: NavController,
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
        },
        bottomBar = {
            BottomMenu(
                navController = navController,
                uid = navData.uid,
                email = navData.email
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
                    .clickable {
                        navController.navigate(
                            FavMovieScreenDataObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.poster_fav_films),
                    contentDescription = "Постер фильма",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Фильмы",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
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
                    .clickable {
                        navController.navigate(
                            FavCartoonScreenDataObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.poster_fav_cartoon),
                    contentDescription = "Постер мультфильма",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Мультфильмы",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
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
                    .clickable {
                        navController.navigate(
                            FavSeriesScreenDataObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.poster_fav_series),
                    contentDescription = "Постер сериала",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Сериалы",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
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
                    .clickable {
                        navController.navigate(
                            FavBookScreenDataObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.poster_fav_book),
                    contentDescription = "Постер книги",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Книги",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }
    }
}