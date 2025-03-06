package com.example.films_shop.main_screen.screens

import MovieViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.films_shop.main_screen.objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.objects.LoginScreenObject
import com.example.films_shop.main_screen.objects.MainScreenDataObject
import com.example.films_shop.ui.theme.BookShopTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Suppress("IMPLICIT_CAST_TO_ANY") // это связано с тем что путь не в строком формате
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movieViewModel: MovieViewModel by viewModels() // <-- Создаем ViewModel
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) MainScreenDataObject(
            currentUser.uid,
            currentUser.email ?: ""
        ) else LoginScreenObject
        //val testStartDestination = HomeScreenObject

        setContent {
            BookShopTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                )
                {
                    composable<LoginScreenObject>
                    {
                        LoginScreen { navData ->
                            navController.navigate(navData) {
                                popUpTo(LoginScreenObject) { inclusive = true }
                            }
                        }
                    }
                    composable<MainScreenDataObject>
                    { navEntry ->
                        val navData = navEntry.toRoute<MainScreenDataObject>()
                        MainScreen(
                            navData,
                            movieViewModel,
                            onExitClick = {
                                Firebase.auth.signOut()
                                navController.navigate(LoginScreenObject) {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                }
                            },
                            onMovieDetailsClick = { movie ->
                                navController.navigate(
                                    DetailsNavMovieObject(
                                        title = movie.name ?: "Неизвестно",
                                        genre = movie.genres?.joinToString(", ") { it.name } ?: "Неизвестно",
                                        year = movie.year?.toString() ?: "Неизвестно",
                                        director = "Неизвестно",
                                        description = movie.description ?: "Описание отсутствует",
                                        imageUrl = movie.poster?.url ?: ""
                                    )
                                )
                            }
                        )
                    }
                    composable<DetailsNavMovieObject>
                    { navEntry ->
                        val navData = navEntry.toRoute<DetailsNavMovieObject>()
                        DetailsMovieScreen(navData)
                    }
                }
            }
        }
    }
}

