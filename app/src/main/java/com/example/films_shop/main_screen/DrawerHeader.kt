package com.example.films_shop.main_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.films_shop.R

@Composable
fun DrawerHeader(email: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(colorResource(id = R.color.custom_blue)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Image(
            modifier = Modifier.height(110.dp),
            painter = painterResource(id = R.drawable.img),contentDescription = "logo"
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Menu",
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = custom_font,
        )
        Text(
            text = email,
            color = Color.White,
            fontSize = 15.sp,
            fontFamily = custom_font,
        )
    }
}