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
        icon = R.drawable.home_logo,
    )
    object Account : BottomMenuItem(
        route = "account",
        title = "Account",
        icon = R.drawable.account_logo,
    )
    object Settings : BottomMenuItem(
        route = "settings",
        title = "Settings",
        icon = R.drawable.baseline_app_settings_alt_24,
    )
}