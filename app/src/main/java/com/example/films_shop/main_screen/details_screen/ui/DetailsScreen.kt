package com.example.films_shop.main_screen.details_screen.ui

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.films_shop.R
import com.example.films_shop.main_screen.custom_font
import com.example.films_shop.main_screen.details_screen.data.DetailsNavObject
import com.example.films_shop.ui.theme.ButtonColor

@Preview(showBackground = true)
@Composable
fun DetailsScreen(
    navObject: DetailsNavObject = DetailsNavObject()
) {

    val base64Image = Base64.decode(navObject.imageUrl, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(
        base64Image, 0,
        base64Image.size
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Column()
        {
            AsyncImage(
                model = bitmap,
                contentDescription = "image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    text = navObject.title,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    fontFamily = custom_font
                )
                Text(
                    text = "Жанр: ",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = custom_font
                )
                Text(
                    text = navObject.genre,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = custom_font
                )
                Text(
                    text = "Год: ",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = custom_font
                )
                Text(
                    text = navObject.year,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = custom_font
                )
                Text(
                    text = "Режиссер: ",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = custom_font
                )
                Text(
                    text = navObject.director,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = custom_font
                )
            }
            Text(
                text = navObject.description,
                color = Color.Black,
                fontSize = 16.sp,
                fontFamily = custom_font
            )
        }
        Button(
            onClick = {

            },
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonColor
            )
        )
        {
            Text(text = "text",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = custom_font,
                color = Color.Black)
        }
    }
}