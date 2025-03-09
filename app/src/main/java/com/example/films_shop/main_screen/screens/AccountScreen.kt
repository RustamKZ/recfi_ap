package com.example.films_shop.main_screen.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.objects.AccountDetailsObject


@Composable
fun AccountDetailsScreen(
    navController: NavController,
    navData: AccountDetailsObject,
    showBottomBar: Boolean = true,
    onExitClick: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomMenu(
                    navController = navController,
                    uid = navData.uid,
                    email = navData.email
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "User Info",
                fontFamily = custom_font
            )
            Text(
                text = "Email: ${navData.email}",
                fontFamily = custom_font
            )
            Button(onClick = {
                onExitClick()
            }) {
                Text(
                    text = "Выйти",
                    fontFamily = custom_font
                )
            }
        }
    }
}