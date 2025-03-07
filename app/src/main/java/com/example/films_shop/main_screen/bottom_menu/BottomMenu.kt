package com.example.films_shop.main_screen.bottom_menu

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.films_shop.main_screen.objects.AccountDetailsObject
import com.example.films_shop.main_screen.objects.FavMovieScreenDataObject
import com.example.films_shop.main_screen.objects.MainScreenDataObject

@Composable
fun BottomMenu(
    navController: NavController,
    uid: String,
    email: String
) {
    val items = listOf(
        BottomMenuItem.Home,
        BottomMenuItem.Account,
        BottomMenuItem.Favourite
    )
    val selectedItem = remember { mutableStateOf("Home") }

    NavigationBar(
        containerColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedItem.value == item.title,
                onClick = {
                    selectedItem.value = item.title
                    when (item) {
                        is BottomMenuItem.Home -> navController.navigate(MainScreenDataObject(uid, email))
                        is BottomMenuItem.Account -> navController.navigate(
                            AccountDetailsObject(uid, email) // Передаем данные
                        )
                        is BottomMenuItem.Favourite -> navController.navigate(
                            FavMovieScreenDataObject(uid, email)
                        )
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) }
            )
        }
    }
}


