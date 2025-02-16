package com.example.films_shop.main_screen.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.films_shop.main_screen.custom_font
import com.example.films_shop.ui.theme.ButtonColor

@Composable
fun LoginButton(
    text:String,
    onClick: () -> Unit
) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier.fillMaxWidth(0.5f),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColor
        )
    )
    {
        Text(text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = custom_font,
            color = Color.Black)
    }
}