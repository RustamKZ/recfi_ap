package com.example.films_shop.main_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.films_shop.R
import com.example.films_shop.main_screen.data.admin_logic.isAdmin

val custom_font = FontFamily(
    Font(R.font.custom_font, FontWeight.Normal),
)

@Composable
fun DrawerBody(
    onAdmin: (Boolean) -> Unit = {},
    onAdminClick: () -> Unit = {},
    onFavClick: () -> Unit = {},
    onGenreClick: (String) -> Unit = {},
) {
    val categoriesList = listOf(
        "Favorites",
        "All",
        "Drama",
        "Comedy",
        "Action",
        "Horror"
    )
    val isAdminState = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        isAdmin { isAdmin ->
            isAdminState.value = isAdmin
            onAdmin(isAdmin)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(colorResource(R.color.custom_blue))
    )
    {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.films_background_collage),
            contentDescription = "ImageBackgroundDrawerBody",
            alpha = 0.2f,
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Box(
                modifier = Modifier // Просто линия
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colorResource(R.color.custom_black))
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Text(
                "Categories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = custom_font,
                color = Color.Black
            )
            Spacer(
                modifier = Modifier.height(16.dp) // Просто пропуск
            )
            Box(
                modifier = Modifier // Просто линия
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colorResource(R.color.custom_black))
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth()

            ) {
                items(categoriesList)
                { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (categoriesList[0] == item)
                                {
                                    onFavClick()
                                } else {
                                    onGenreClick(item)
                                }
                    }
                    ) // Поместили все в Column для слушателя клика
                    {
                        Spacer(
                            modifier = Modifier.height(16.dp) // Просто пропуск
                        )
                        Text(color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_font,
                            text = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth()
                        )
                        Spacer(
                            modifier = Modifier.height(16.dp) // Просто пропуск
                        )
                        Box(
                            modifier = Modifier // Просто линия
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(colorResource(R.color.custom_black))
                        )
                    }

                }
            }
            Spacer(
                modifier = Modifier.height(16.dp) // Просто пропуск
            )
            if (isAdminState.value)
            Button(
                onClick = {
                    onAdminClick()
                },
                modifier = Modifier.fillMaxWidth(0.5f).padding(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            )
            {
                Text(text = "Admin panel",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = custom_font,
                    color = Color.White)
            }
        }

    }
}