package com.example.films_shop.main_screen.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.films_shop.main_screen.screens.custom_font

@Composable
fun LoginButton(
    text:String,
    backColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier.fillMaxWidth().height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = textColor
        )
    )
    {
        Text(text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = custom_font,
            color = backColor)
    }
}