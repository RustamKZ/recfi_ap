package com.example.films_shop.main_screen.screens

import MovieViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.example.films_shop.main_screen.api.MovieitemUi
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.bottom_menu.MainViewModel
import com.example.films_shop.main_screen.login.RoundedCornerTextField
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavMovieObject
import com.example.films_shop.main_screen.objects.main_screens_objects.SearchScreenDataObject
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.BackGroundColorButton
import com.example.films_shop.ui.theme.BackGroundColorButtonLightGray
import com.example.films_shop.ui.theme.backColorChatCard
import com.example.films_shop.ui.theme.mainColorUiGreen
import androidx.compose.ui.text.TextStyle


@Composable
fun RoundedCornerTextFieldChat(
    text: String,
    label: String,
    backColorTextField: Color,
    сolorText: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = text,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontFamily = custom_font,
            fontSize = 18.sp,
            color = сolorText
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon",
                tint = mainColorUiGreen
            )
        },
        placeholder = {
            Text(
                text = label,
                color = Color.Gray,
                fontFamily = custom_font,
                fontSize = 16.sp
            )
        },
        singleLine = singleLine,
        maxLines = maxLines,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = backColorTextField,
            focusedContainerColor = backColorTextField,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = mainColorUiGreen,
            cursorColor = Color.DarkGray,
        ),
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp)
    )
}


@Composable
fun SearchScreen(
    navData: SearchScreenDataObject,
    navController: NavController,
    viewModel: MainViewModel,
    movieViewModel: MovieViewModel,
    noOpNestedScrollConnection: NestedScrollConnection,
    showBottomBar: Boolean,
) {
    val isDark = isSystemInDarkTheme()
    val backColor = if (isDark) BackGroundColor else Color.White
    val buttonBackgroundColor =
        if (isDark) backColorChatCard else Color.White
    val textColor = if (isDark) Color.White else BackGroundColor
    val searchColor = if (isDark) Color.Black else Color.White
    val colorText = if (isDark) Color.White else Color.Black
    val backColorTextField = if (isDark) BackGroundColorButton else BackGroundColorButtonLightGray
    val query = remember { mutableStateOf("") }

    val movies = movieViewModel.searchMovies(query.value).collectAsLazyPagingItems()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backColor)
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomMenu(
                        navController = navController,
                        uid = navData.uid,
                        email = navData.email,
                        selectedTab = viewModel.selectedTab,
                        onTabSelected = { viewModel.onTabSelected(it) }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(noOpNestedScrollConnection)
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Поиск по названию",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = custom_font,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                RoundedCornerTextFieldChat(
                    text = query.value,
                    label = "Введите название фильма",
                    backColorTextField = searchColor,
                    сolorText = colorText
                ) {
                    query.value = it
                }

                Spacer(Modifier.height(10.dp))
                if (query.value.isNotBlank()) {

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(movies.itemCount) { index ->
                            movies[index]?.let { movie ->
                                val type_ch = when (movie.type) {
                                    "movie" -> "Фильм"
                                    "tv-series" -> "Сериал"
                                    "cartoon" -> "Мультфильм"
                                    else -> "Неизвестно"
                                }
                                val genres_pre = movie.genres?.joinToString(", ") { it.name }
                                    ?: "Неизвестно"
                                val genres_ch = genres_pre.split(",")
                                    .mapIndexed { index, genre ->
                                        if (index == 0) genre.trim().replaceFirstChar { it.uppercaseChar() }
                                        else genre.trim()
                                    }
                                    .joinToString(", ")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            buttonBackgroundColor,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            navController.navigate(
                                                DetailsNavMovieObject(
                                                    id = movie.id ?: "",
                                                    tmdbId = movie.externalId?.tmdb ?: 0,
                                                    title = movie.name ?: "Неизвестно",
                                                    type = movie.type ?: "Неизвестно",
                                                    genre = movie.genres?.joinToString(", ") { it.name }
                                                        ?: "Неизвестно",
                                                    year = movie.year ?: "Неизвестно",
                                                    description = movie.description
                                                        ?: "Описание отсутствует",
                                                    imageUrl = movie.poster?.url ?: "",
                                                    backdropUrl = movie.backdrop?.url ?: "",
                                                    ratingKp = movie.rating?.kp ?: 0.0,
                                                    ratingImdb = movie.rating?.imdb ?: 0.0,
                                                    votesKp = movie.votes?.kp ?: 0,
                                                    votesImdb = movie.votes?.imdb ?: 0,
                                                    persons = movie.persons?.joinToString(", ") { "${it.name}|${it.photo}" }
                                                        ?: "Неизвестно",
                                                    isFavorite = movie.isFavorite,
                                                    isBookMark = movie.isBookMark,
                                                    isRated = movie.isRated,
                                                    userRating = movie.userRating ?: 0
                                                )
                                            )
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            movie.backdrop?.url ?: movie.poster?.url
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(width = 108.dp, height = 72.dp)
                                            .clip(RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = movie.name ?: "Неизвестно",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textColor,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = "${genres_ch} · ${movie.year} · ${type_ch}",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Outlined.ChevronRight,
                                        contentDescription = null,
                                        tint = mainColorUiGreen, // зелёная галочка
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        when (movies.loadState.append) {
                            is LoadState.Loading -> {
                                item {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                                }
                            }

                            is LoadState.Error -> {
                                item {
                                    Text("Ошибка при загрузке данных", color = Color.Red)
                                }
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}
