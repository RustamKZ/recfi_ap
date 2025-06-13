package com.example.films_shop.main_screen.screens

import AccountFriendsScreen
import AddFriendWithRequestsScreen
import ContentType
import MovieViewModel
import UserCollectionBookScreen
import UserCollectionMovieScreen
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.api.recomendations.RecommendationViewModel
import com.example.films_shop.main_screen.bottom_menu.MainViewModel
import com.example.films_shop.main_screen.objects.auth_screens_objects.AccountDetailsObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.AddFriendObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.ChatFriendsObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.FriendsAccountObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.ImageAccountObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.LoginScreenObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.SettingsAccountObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.SingleChatScreenDestination
import com.example.films_shop.main_screen.objects.cold_start.ColdStartScreenDataObject
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavBookObject
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.objects.fav_screens_objects.FavBookScreenDataObject
import com.example.films_shop.main_screen.objects.fav_screens_objects.FavCartoonScreenDataObject
import com.example.films_shop.main_screen.objects.fav_screens_objects.FavMovieScreenDataObject
import com.example.films_shop.main_screen.objects.fav_screens_objects.FavScreenDataObject
import com.example.films_shop.main_screen.objects.fav_screens_objects.FavSeriesScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.BookScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.CartoonScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MovieScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.SearchScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.SeriesScreenDataObject
import com.example.films_shop.main_screen.objects.rec_objects.RecBookScreenDataObject
import com.example.films_shop.main_screen.objects.rec_objects.RecCartoonScreenDataObject
import com.example.films_shop.main_screen.objects.rec_objects.RecMovieScreenDataObject
import com.example.films_shop.main_screen.objects.rec_objects.RecTvSeriesScreenDataObject
import com.example.films_shop.main_screen.objects.rec_objects.genre_authors.RecBookScreenAuthorObject
import com.example.films_shop.main_screen.objects.rec_objects.genre_authors.RecCartoonScreenGenreObject
import com.example.films_shop.main_screen.objects.rec_objects.genre_authors.RecMovieScreenGenreObject
import com.example.films_shop.main_screen.objects.rec_objects.genre_authors.RecTvSeriesScreenGenreObject
import com.example.films_shop.main_screen.screens.account.AccountImageScreen
import com.example.films_shop.main_screen.screens.account.AccountScreen
import com.example.films_shop.main_screen.screens.account.AccountSettingsScreen
import com.example.films_shop.main_screen.screens.account.ChatScreen
import com.example.films_shop.main_screen.screens.account.LoginScreen
import com.example.films_shop.main_screen.screens.account.SingleChatScreen
import com.example.films_shop.main_screen.screens.cold_start.ColdStartScreen
import com.example.films_shop.main_screen.screens.favourite_screens.FavScreen
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.BookShopTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Suppress("IMPLICIT_CAST_TO_ANY") // это связано с тем что путь не в строком формате
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noOpNestedScrollConnection = object : NestedScrollConnection {}
        val recViewModel: RecommendationViewModel by viewModels()
        val movieViewModel: MovieViewModel by viewModels()
        val bookViewModel: BookViewModel by viewModels()
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) MainScreenDataObject(
            currentUser.uid,
            currentUser.email ?: "",
            showLoadingAnimation = true
        ) else LoginScreenObject
        //val startDestination = TestScreenObject
        setContent {
            val window = (this@MainActivity).window
            val isDarkTheme = isSystemInDarkTheme()
            SideEffect {
                window.statusBarColor = if (isDarkTheme) BackGroundColor.toArgb() else Color.White.toArgb()
                WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !isDarkTheme
                window.navigationBarColor = if (isDarkTheme) BackGroundColor.toArgb() else Color.White.toArgb()
                WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = !isDarkTheme
            }
            BookShopTheme() {
                val navController = rememberNavController()
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                    state = rememberTopAppBarState()
                )
                val viewModel: MainViewModel = viewModel()
                Scaffold(
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    )
                    {
                        composable<LoginScreenObject>
                        {
                            LoginScreen(
                                onNavigateToMainScreen = { mainData ->
                                    navController.navigate(mainData) {
                                        popUpTo(LoginScreenObject) { inclusive = true }
                                    }
                                },
                                onNavigateToColdStartScreen = { coldStartData ->
                                    navController.navigate(coldStartData) {
                                        popUpTo(LoginScreenObject) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable<ColdStartScreenDataObject>
                        {
                            navEntry ->
                            val navData = navEntry.toRoute<ColdStartScreenDataObject>()
                            ColdStartScreen(navController,navData)
                        }
                        composable<SearchScreenDataObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<SearchScreenDataObject>()
                            SearchScreen(
                                navData,
                                navController,
                                viewModel,
                                movieViewModel,
                                noOpNestedScrollConnection,
                                true
                            )
                        }
                        composable<MainScreenDataObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<MainScreenDataObject>()
                            MainScreen(
                                navData,
                                movieViewModel,
                                bookViewModel,
                                recViewModel,
                                navController,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                viewModel
                            )
                        }
                        composable<FavScreenDataObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<FavScreenDataObject>()
                            FavScreen(
                                navData = MainScreenDataObject(navData.uid, navData.email),
                                navController = navController,
                                viewModel
                            )
                        }
                        composable<FavMovieScreenDataObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<FavMovieScreenDataObject>()
                            UserCollectionMovieScreen(
                                navData = MainScreenDataObject(navData.uid, navData.email),
                                movieViewModel,
                                navController = navController,
                                false,
                                true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.MOVIES,
                                viewModel
                            )
                        }
                        composable<FavSeriesScreenDataObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<FavSeriesScreenDataObject>()
                            UserCollectionMovieScreen(
                                navData = MainScreenDataObject(navData.uid, navData.email),
                                movieViewModel,
                                navController = navController,
                                false,
                                true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.TV_SERIES,
                                viewModel
                            )
                        }
                        composable<FavCartoonScreenDataObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<FavCartoonScreenDataObject>()
                            UserCollectionMovieScreen(
                                navData = MainScreenDataObject(navData.uid, navData.email),
                                movieViewModel,
                                navController = navController,
                                false,
                                true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.CARTOONS,
                                viewModel
                            )
                        }
                        composable<FavBookScreenDataObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<FavBookScreenDataObject>()
                            UserCollectionBookScreen(
                                navData = MainScreenDataObject(navData.uid, navData.email),
                                bookViewModel,
                                navController = navController,
                                false,
                                true,
                                scrollBehavior,
                                viewModel
                            )
                        }
                        composable<MovieScreenDataObject> { navEntry ->
                            val navData = navEntry.toRoute<MovieScreenDataObject>()
                            Log.d("TestNavData", "Before Movie Screen 2: {$navData.email}")
                            MovieScreen(
                                navController = navController,
                                movieViewModel = movieViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.MOVIES,
                                viewModel
                            )
                        }
                        composable<RecMovieScreenDataObject> { navEntry ->
                            val navData = navEntry.toRoute<RecMovieScreenDataObject>()
                            RecMovieScreen(
                                navController = navController,
                                movieViewModel = movieViewModel,
                                recViewModel = recViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.MOVIES,
                                viewModel
                            )
                        }
                        composable<RecMovieScreenGenreObject> { navEntry ->
                            val navData = navEntry.toRoute<RecMovieScreenDataObject>()
                            RecMovieScreen(
                                navController = navController,
                                movieViewModel = movieViewModel,
                                recViewModel = recViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.MOVIES,
                                viewModel,
                                true
                            )
                        }
                        composable<SeriesScreenDataObject> { navEntry ->
                            val navData = navEntry.toRoute<MovieScreenDataObject>()
                            MovieScreen(
                                navController = navController,
                                movieViewModel = movieViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.TV_SERIES,
                                viewModel
                            )
                        }
                        composable<RecTvSeriesScreenDataObject> { navEntry ->
                            val navData = navEntry.toRoute<RecMovieScreenDataObject>()
                            RecMovieScreen(
                                navController = navController,
                                movieViewModel = movieViewModel,
                                recViewModel = recViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.TV_SERIES,
                                viewModel
                            )
                        }
                        composable<RecTvSeriesScreenGenreObject> { navEntry ->
                            val navData = navEntry.toRoute<RecMovieScreenDataObject>()
                            RecMovieScreen(
                                navController = navController,
                                movieViewModel = movieViewModel,
                                recViewModel = recViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.TV_SERIES,
                                viewModel,
                                true
                            )
                        }
                        composable<CartoonScreenDataObject> { navEntry ->
                            val navData = navEntry.toRoute<MovieScreenDataObject>()
                            MovieScreen(
                                navController = navController,
                                movieViewModel = movieViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.CARTOONS,
                                viewModel
                            )
                        }
                        composable<RecCartoonScreenDataObject> { navEntry ->
                            val navData = navEntry.toRoute<RecMovieScreenDataObject>()
                            RecMovieScreen(
                                navController = navController,
                                movieViewModel = movieViewModel,
                                recViewModel = recViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.CARTOONS,
                                viewModel
                            )
                        }
                        composable<RecCartoonScreenGenreObject> { navEntry ->
                            val navData = navEntry.toRoute<RecMovieScreenDataObject>()
                            RecMovieScreen(
                                navController = navController,
                                movieViewModel = movieViewModel,
                                recViewModel = recViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                ContentType.CARTOONS,
                                viewModel,
                                true
                            )
                        }
                        composable<BookScreenDataObject> { navEntry ->
                            val navData = navEntry.toRoute<BookScreenDataObject>()
                            BookScreen(
                                navController = navController,
                                bookViewModel = bookViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                viewModel
                            )
                        }
                        composable<RecBookScreenDataObject> { navEntry ->
                            val navData = navEntry.toRoute<RecBookScreenDataObject>()
                            RecBookScreen(
                                navController = navController,
                                bookViewModel = bookViewModel,
                                recViewModel = recViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                viewModel
                            )
                        }
                        composable<RecBookScreenAuthorObject> { navEntry ->
                            val navData = navEntry.toRoute<RecBookScreenDataObject>()
                            RecBookScreen(
                                navController = navController,
                                bookViewModel = bookViewModel,
                                recViewModel = recViewModel,
                                navData = navData,
                                showTopBar = false,
                                showBottomBar = true,
                                scrollBehavior,
                                noOpNestedScrollConnection,
                                viewModel,
                                true
                            )
                        }
                        // Экран аккаунта
                        composable<AccountDetailsObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<AccountDetailsObject>()
                            AccountScreen(
                                navController = navController,
                                navData,
                                showBottomBar = true,
                                viewModel
                            ) {
                                Firebase.auth.signOut()
                                navController.navigate(LoginScreenObject) {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                        // Экран настроек аккаунта
                        composable<SettingsAccountObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<SettingsAccountObject>()
                            AccountSettingsScreen(
                                navData
                            )
                        }
                        // Выбор аватарки
                        composable<ImageAccountObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<ImageAccountObject>()
                            AccountImageScreen(
                                navData
                            ) {
                                navController.popBackStack()
                            }
                        }
                        // Экран добавления в друзья
                        composable<AddFriendObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<AddFriendObject>()
                            AddFriendWithRequestsScreen(navData)
                        }
                        // Экран друзей
                        composable<FriendsAccountObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<FriendsAccountObject>()
                            AccountFriendsScreen(navData)
                        }
                        composable<ChatFriendsObject> { navEntry ->
                            val navData = navEntry.toRoute<ChatFriendsObject>()
                            ChatScreen(navController, navData)
                        }
                        composable<SingleChatScreenDestination> { navEntry ->
                            val args = navEntry.toRoute<SingleChatScreenDestination>()
                            SingleChatScreen(
                                navController = navController,
                                movieViewModel = movieViewModel,
                                bookViewModel = bookViewModel,
                                uid = args.uid,
                                friendUid = args.friendUid,
                                friendName = args.friendName,
                                friendPhotoUrl = args.photoUrl
                            )
                        }

                        // Экран деталей фильма
                        composable<DetailsNavMovieObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<DetailsNavMovieObject>()
                            Log.d("TestNavData", "Before details movie screen 10: ${Firebase.auth.currentUser?.email}")
                            TestDetailsMovieScreen(
                                navObject = navData,
                                navData = MovieScreenDataObject(
                                    uid = Firebase.auth.currentUser?.uid ?: "",
                                    email = Firebase.auth.currentUser?.email ?: ""
                                ),
                                movieViewModel,
                                recViewModel,
                                navController = navController
                            )
                        }
                        composable<DetailsNavBookObject>
                        { navEntry ->
                            val navData = navEntry.toRoute<DetailsNavBookObject>()
                            DetailsBookScreen(
                                navObject = navData,
                                navData = BookScreenDataObject(
                                    uid = currentUser?.uid ?: "",
                                    email = currentUser?.email ?: ""
                                ),
                                bookViewModel,
                                recViewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

