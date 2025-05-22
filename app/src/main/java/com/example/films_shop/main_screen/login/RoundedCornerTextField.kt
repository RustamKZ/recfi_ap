package com.example.films_shop.main_screen.login

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.films_shop.main_screen.screens.custom_font
import com.example.films_shop.ui.theme.BorderColor

@Composable
fun RoundedCornerTextField(
    maxLines: Int = 1,
    singLine: Boolean = true,
    text: String,
    label: String,
    backColorTextField: Color,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = text,
        textStyle = TextStyle(fontFamily = custom_font, fontSize = 25.sp),
        onValueChange =  {
            onValueChange(it)
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = backColorTextField,
            focusedContainerColor = backColorTextField,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = label, color = Color.Gray, fontFamily = custom_font, fontSize = 20.sp)
        },
        singleLine = singLine,
        maxLines = maxLines
    )
}