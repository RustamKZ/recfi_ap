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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.films_shop.main_screen.objects.auth_screens_objects.AccountDetailsObject
import com.example.films_shop.main_screen.objects.fav_screens_objects.FavScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.SearchScreenDataObject
import com.example.films_shop.ui.theme.mainColorUiGreen

class MainViewModel : ViewModel() {
    var selectedTab by mutableStateOf("Home")
        private set

    fun onTabSelected(tab: String) {
        selectedTab = tab
    }
}


@Composable
fun BottomMenu(
    navController: NavController,
    uid: String,
    email: String,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        BottomMenuItem.Home,
        BottomMenuItem.Search,
        BottomMenuItem.Account,
        BottomMenuItem.Favourite
    )
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) Color.Black else Color.White
    val iconColor = if (isDark) Color.White else Color.Black
    val textColor = if (isDark) Color.White else Color.Black

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(70.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
    ) {
        NavigationBar(containerColor = Color.Transparent) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = selectedTab == item.title,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = mainColorUiGreen,
                        unselectedIconColor = iconColor,
                        selectedTextColor = iconColor,
                        unselectedTextColor = textColor,
                        indicatorColor = Color.Transparent
                    ),
                    onClick = {
                        onTabSelected(item.title)
                        when (item) {
                            is BottomMenuItem.Home -> navController.navigate(MainScreenDataObject(uid, email))
                            is BottomMenuItem.Search -> navController.navigate(SearchScreenDataObject(uid, email))
                            is BottomMenuItem.Account -> navController.navigate(AccountDetailsObject(uid, email))
                            is BottomMenuItem.Favourite -> navController.navigate(FavScreenDataObject(uid, email))
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


