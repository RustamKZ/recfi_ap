package com.example.films_shop.main_screen.login.data_nav

import kotlinx.serialization.Serializable

@Serializable
data class MainScreenDataObject(
    val uid: String = "",
    val email: String = ""
)
