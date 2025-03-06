package com.example.films_shop.main_screen.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onTrendFilmsClick: () -> Unit,
    paddingValues: PaddingValues
) {
    val pagerState = rememberPagerState(pageCount = { 10 })
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(PaddingValues(top = paddingValues.calculateTopPadding())),
    ) {
        Column(

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Сейчас в тренде",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp)
                )
                Button(
                    onClick = {
                        Log.d("MyLog", "Click on see all")
                        onTrendFilmsClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, // Прозрачный фон
                        contentColor = Color.Black // Цвет текста
                    ),
                    contentPadding = PaddingValues(0.dp), // Убираем отступы внутри кнопки
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .wrapContentSize()
                ) {
                    Text(
                        text = "Посмотреть",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                    )
                }

            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(10) { index ->
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(120.dp)
                            .height(200.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Элемент $index")
                    }
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Рекомендуем",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp)
                )
                Button(
                    onClick = { /* Действие при нажатии */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, // Прозрачный фон
                        contentColor = Color.Black // Цвет текста
                    ),
                    contentPadding = PaddingValues(0.dp), // Убираем отступы внутри кнопки
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .wrapContentSize()
                ) {
                    Text(
                        text = "Посмотреть",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                    )
                }

            }
            // Сам "LazyRow" с свайпами
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(start = 8.dp, end = 8.dp)
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            when (page) {
                                0 -> Color.Gray
                                1 -> Color.Blue
                                2 -> Color.Green
                                3 -> Color.Yellow
                                else -> Color.Gray
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Страница $page", color = Color.White)
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(16.dp)
            ) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 10.dp else 8.dp) // Активная точка больше
                            .background(
                                if (pagerState.currentPage == index) Color.DarkGray else Color.LightGray,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
        Column(

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Сериалы",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp)
                )
                Button(
                    onClick = { /* Действие при нажатии */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, // Прозрачный фон
                        contentColor = Color.Black // Цвет текста
                    ),
                    contentPadding = PaddingValues(0.dp), // Убираем отступы внутри кнопки
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .wrapContentSize()
                ) {
                    Text(
                        text = "Посмотреть",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                    )
                }

            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(10) { index ->
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(120.dp)
                            .height(200.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Элемент $index")
                    }
                }
            }
        }
        Column(

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Фильмы",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp)
                )
                Button(
                    onClick = { /* Действие при нажатии */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, // Прозрачный фон
                        contentColor = Color.Black // Цвет текста
                    ),
                    contentPadding = PaddingValues(0.dp), // Убираем отступы внутри кнопки
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .wrapContentSize()
                ) {
                    Text(
                        text = "Посмотреть",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                    )
                }

            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(10) { index ->
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(120.dp)
                            .height(200.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Элемент $index")
                    }
                }
            }
        }
    }

}