package com.example.films_shop.main_screen.screens.account

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.films_shop.main_screen.objects.auth_screens_objects.FriendsAccountObject

@Composable
fun AccountFriendsScreen(
    navData: FriendsAccountObject,
) {
    Box() {
        Text(text = "Friends Screen")
    }
}