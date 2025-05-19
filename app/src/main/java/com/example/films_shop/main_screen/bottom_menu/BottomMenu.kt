package com.example.films_shop.main_screen.bottom_menu

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.films_shop.main_screen.objects.auth_screens_objects.AccountDetailsObject
import com.example.films_shop.main_screen.objects.fav_screens_objects.FavScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.ui.theme.AppColor

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
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) Color.Black else Color.White
    val iconColor = if (isDark) Color.White else Color.Black
    val textColor = if (isDark) Color.White else Color.Black
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp) // отступы вокруг
            .height(70.dp)
            .clip(RoundedCornerShape(16.dp)) // скругляем углы
            .background(backgroundColor) // фон с закруглениями
    ) {
        NavigationBar(
            containerColor = Color.Transparent
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = selectedItem.value == item.title,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = iconColor,      // цвет иконки выбранного
                        unselectedIconColor = iconColor,    // цвет иконки невыбранного
                        selectedTextColor = iconColor,      // если есть label, цвет текста выбранного
                        unselectedTextColor = textColor,    // цвет текста невыбранного
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

