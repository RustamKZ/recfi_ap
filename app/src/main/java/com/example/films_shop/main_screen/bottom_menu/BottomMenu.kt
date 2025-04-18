package com.example.films_shop.main_screen.bottom_menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.films_shop.main_screen.objects.AccountDetailsObject
import com.example.films_shop.main_screen.objects.FavScreenDataObject
import com.example.films_shop.main_screen.objects.MainScreenDataObject
import com.example.films_shop.ui.theme.AppColor
import com.example.films_shop.ui.theme.BottomColor

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
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp) // отступы вокруг
            .height(70.dp)
            .clip(RoundedCornerShape(16.dp)) // скругляем углы
            .background(AppColor) // фон с закруглениями
    ) {
        NavigationBar(
            containerColor = Color.Transparent
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = selectedItem.value == item.title,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,      // цвет иконки выбранного
                        unselectedIconColor = Color.Black,    // цвет иконки невыбранного
                        selectedTextColor = Color.Black,      // если есть label, цвет текста выбранного
                        unselectedTextColor = Color.Black,    // цвет текста невыбранного
                        indicatorColor = Color.Transparent    // цвет индикатора (подсветки) выбранного — прозрачный
                    ),
                    onClick = {
                        selectedItem.value = item.title
                        when (item) {
                            is BottomMenuItem.Home -> navController.navigate(
                                MainScreenDataObject(
                                    uid,
                                    email
                                )
                            )

                            is BottomMenuItem.Account -> navController.navigate(
                                AccountDetailsObject(uid, email) // Передаем данные
                            )

                            is BottomMenuItem.Favourite -> navController.navigate(
                                FavScreenDataObject(uid, email)
                            )
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                )
            }
        }
    }
}

