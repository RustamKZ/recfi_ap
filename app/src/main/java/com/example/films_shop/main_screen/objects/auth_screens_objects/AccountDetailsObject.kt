package com.example.films_shop.main_screen.objects.auth_screens_objects

import kotlinx.serialization.Serializable

@Serializable
data class AccountDetailsObject (
    val uid: String = "",
    val email: String = ""
)