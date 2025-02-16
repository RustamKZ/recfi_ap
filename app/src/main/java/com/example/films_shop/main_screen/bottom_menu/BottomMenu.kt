package com.example.films_shop.main_screen.bottom_menu

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource

@Composable
fun BottomMenu(
    onAccountClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val items = listOf(
        BottomMenuItem.Home,
        BottomMenuItem.Account,
        BottomMenuItem.Settings,
    )
    val selectedItem = remember { mutableStateOf("Home") }
    NavigationBar() {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedItem.value == item.title,
                onClick = {
                    selectedItem.value = item.title
                    when(item.title) {
                        BottomMenuItem.Home.title -> onHomeClick()
                        BottomMenuItem.Account.title -> onAccountClick()
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = "Home screen"
                    )
                },
                label = {
                    Text(text = item.title)
                }
            )
        }
    }
}