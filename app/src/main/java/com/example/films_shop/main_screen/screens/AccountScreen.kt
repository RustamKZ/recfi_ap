package com.example.films_shop.main_screen.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.films_shop.main_screen.objects.MainScreenDataObject

@Composable
fun AccountDetailsScreen(
    navData: MainScreenDataObject,
    onExitClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Здесь можно вывести данные пользователя
        Text(
            text = "User Info",
            fontFamily = custom_font
        )
        Text(
            text = "Email: ${navData.email}",
            fontFamily = custom_font
        )
        Button(onClick ={
            onExitClick()
        }) {
            Text(
                text = "Выйти",
                fontFamily = custom_font
            )
        }
        // Другие поля с данными пользователя
    }
}
