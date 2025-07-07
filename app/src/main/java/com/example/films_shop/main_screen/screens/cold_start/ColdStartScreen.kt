package com.example.films_shop.main_screen.screens.cold_start

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.films_shop.main_screen.Genres.AuthorsGoogle
import com.example.films_shop.main_screen.Genres.GenreKP
import com.example.films_shop.main_screen.Genres.loadUserAuthors
import com.example.films_shop.main_screen.Genres.loadUserGenres
import com.example.films_shop.main_screen.objects.cold_start.ColdStartScreenDataObject
import com.example.films_shop.main_screen.objects.cold_start.GenreSelectionScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun ColdStartScreen(navController: NavController, navData: ColdStartScreenDataObject) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val genreSelectionScreenDataObject = GenreSelectionScreenDataObject(navData.uid, navData.email)
    val mainScreenDataObject = MainScreenDataObject(navData.uid, navData.email, true)
    val db = remember {
        Firebase.firestore
    }
    val selectedGenres = remember { mutableStateListOf<GenreKP>() }
    val selectedAuthors = remember { mutableStateListOf<AuthorsGoogle>() }
    LaunchedEffect(navData.uid) {
        loadUserGenres(db, navData.uid) { loadedGenres->
            if (loadedGenres.isNotEmpty()) {
                selectedGenres.addAll(loadedGenres)
            }
        }
        loadUserAuthors(db, navData.uid) { loadedAuthors ->
            if (loadedAuthors.isNotEmpty()) {
                selectedAuthors.addAll(loadedAuthors)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            count = 4,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> WelcomeScreen(navData.flag) {
                    coroutineScope.launch { pagerState.animateScrollToPage(1) }
                }
                1 -> GenreSelectionScreen(navData.flag, selectedGenres, mainScreenDataObject, navController, genreSelectionScreenDataObject, db) {
                    coroutineScope.launch { pagerState.animateScrollToPage(2) }
                }
                2 -> AuthorsBookSelectScreen(navData.flag, selectedAuthors,mainScreenDataObject, navController, genreSelectionScreenDataObject, db) {
                    coroutineScope.launch { pagerState.animateScrollToPage(3) }
                }
                3 -> EndWelcomeScreen(navData.flag, db, selectedGenres, selectedAuthors, navController, mainScreenDataObject)
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            activeColor = Color.White,
            inactiveColor = Color.Gray
        )
    }
}
