package com.example.films_shop.main_screen.screens

import MovieViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.objects.AccountDetailsObject
import com.example.films_shop.main_screen.objects.BookScreenDataObject
import com.example.films_shop.main_screen.objects.DetailsNavBookObject
import com.example.films_shop.main_screen.objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.objects.FavMovieScreenDataObject
import com.example.films_shop.main_screen.objects.LoginScreenObject
import com.example.films_shop.main_screen.objects.MainScreenDataObject
import com.example.films_shop.main_screen.objects.MovieScreenDataObject
import com.example.films_shop.ui.theme.BookShopTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Suppress("IMPLICIT_CAST_TO_ANY") // это связано с тем что путь не в строком формате
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movieViewModel: MovieViewModel by viewModels()
        val bookViewModel: BookViewModel by viewModels()
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) MainScreenDataObject(
            currentUser.uid,
            currentUser.email ?: ""
        ) else LoginScreenObject
        setContent {
            BookShopTheme {
                val navController = rememberNavController()
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                    state = rememberTopAppBarState()
                )
                Scaffold(
                ) {
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
                                bookViewModel,
                                navController,
                                showTopBar= true,
                                showBottomBar = true,
                                scrollBehavior
                            )
                        }
                        composable<FavMovieScreenDataObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<FavMovieScreenDataObject>()
                            TestFavMovieScreen(
                                navData = MainScreenDataObject(navData.uid, navData.email),
                                movieViewModel = movieViewModel,
                                navController = navController,
                                showTopBar= true,
                                showBottomBar = true,
                                scrollBehavior
                            )
                        }
                        composable<MovieScreenDataObject> { navEntry ->
                            val navData = navEntry.toRoute<MovieScreenDataObject>()
                            TestMovieScreen(
                                navController = navController,
                                movieViewModel = movieViewModel,
                                navData = navData,
                                showTopBar= true,
                                showBottomBar = true,
                                scrollBehavior
                            )
                        }
                        composable<BookScreenDataObject> { navEntry ->
                            val navData = navEntry.toRoute<BookScreenDataObject>()
                            BookScreen(
                                navController = navController,
                                bookViewModel = bookViewModel,
                                navData = navData,
                                showTopBar= true,
                                showBottomBar = true,
                                scrollBehavior
                            )
                        }
                        // Экран аккаунта
                        composable<AccountDetailsObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<AccountDetailsObject>()
                            AccountDetailsScreen(
                                navController = navController,
                                navData,
                                showBottomBar = true,
                            ) {
                                Firebase.auth.signOut()
                                navController.navigate(LoginScreenObject) {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                        // Экран деталей фильма
                        composable<DetailsNavMovieObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<DetailsNavMovieObject>()
                            DetailsMovieScreen(
                                navObject = navData,
                                navData = MovieScreenDataObject(uid = currentUser?.uid ?: "", email = currentUser?.email ?: ""),
                                movieViewModel
                            )
                        }
                        composable< DetailsNavBookObject>
                        {
                            navEntry ->
                            val navData = navEntry.toRoute<DetailsNavBookObject>()
                            DetailsBookScreen(
                                navObject = navData,
                                navData = BookScreenDataObject(uid = currentUser?.uid ?: "", email = currentUser?.email ?: ""),
                                bookViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

