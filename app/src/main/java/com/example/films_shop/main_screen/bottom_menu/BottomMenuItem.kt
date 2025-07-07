package com.example.films_shop.main_screen.bottom_menu

import com.example.films_shop.R

sealed class BottomMenuItem(
    val route: String,
    val title: String,
    val icon: Int,
) {
    object Home : BottomMenuItem(
        route = "home",
        title = "Home",
        icon = R.drawable.baseline_home_24,
    )
    object Search : BottomMenuItem(
        route = "search",
        title = "Search",
        icon = R.drawable.error_avatar,
    )
    object Account : BottomMenuItem(
        route = "account",
        title = "Account",
        icon = R.drawable.baseline_person_24,
    )
    object Favourite : BottomMenuItem(
        route = "favourite",
        title = "favourite",
        icon = R.drawable.favorite_bottom_bar,
    )
}