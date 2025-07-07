package com.example.films_shop.main_screen.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.films_shop.main_screen.api.BookApi.Book
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.api.recomendations.RecommendationViewModel
import com.example.films_shop.main_screen.business_logic.onBookmarkBooks
import com.example.films_shop.main_screen.business_logic.onFavsBooks
import com.example.films_shop.main_screen.business_logic.onRatedBooks
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavBookObject
import com.example.films_shop.main_screen.objects.main_screens_objects.BookScreenDataObject
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.BackGroundColorButton
import com.example.films_shop.ui.theme.BackGroundColorButtonLightGray
import com.example.films_shop.ui.theme.ButtonColor
import com.example.films_shop.ui.theme.backColorChatCard
import com.example.films_shop.ui.theme.mainColorUiGreen
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun RatingItemBook(
    title: String,
    rating: String,
    votes: String,
    buttonTextColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = buttonTextColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = rating,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold,
                color = buttonTextColor,
                fontSize = 35.sp,
            ),
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = "Голосов: $votes",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            fontSize = 15.sp,
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun RatingCardBook(
    averageRating: Double,
    ratingsCount: Int,
    buttonBackgroundColor: Color,
    buttonTextColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = buttonBackgroundColor,
                    shape = RoundedCornerShape(16.dp)
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RatingItemBook(
                title = "",
                rating = "Рейтинг: $averageRating",
                votes = ratingsCount.toString(),
                buttonTextColor = buttonTextColor,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}


@Composable
fun DetailsBookScreen(
    navObject: DetailsNavBookObject = DetailsNavBookObject(),
    navData: BookScreenDataObject? = null,
    bookViewModel: BookViewModel,
    recViewModel: RecommendationViewModel,
    navController: NavController,
) {
    var expanded by remember { mutableStateOf(false) }
    val isFavorite = remember(
        bookViewModel.favoriteBooksState.value,
    ) {
        bookViewModel.isInFavorites(navObject.id)
    }
    val isBookmark = remember(
        bookViewModel.bookmarkBooksState.value,
    ) {
        bookViewModel.isInBookmarks(navObject.id)
    }
    val isRated = remember(
        bookViewModel.ratedBooksState.value,
    ) {
        bookViewModel.isInRated(navObject.id)
    }
    val scrollState = rememberScrollState()
    val db = Firebase.firestore

    val recommendationBooks by recViewModel.recommendationBooks
    val isLoading by recViewModel.isLoading
    val error by recViewModel.error
    val id = navObject.isbn10
    var expandedButton by remember { mutableStateOf(false) }
    // ui stars
    val filledStars = (navObject.userRating / 2).toInt()
    val hasHalfStar = (navObject.userRating / 2) - filledStars >= 0.5
    val emptyStars = 5 - filledStars - if (hasHalfStar) 1 else 0
    // ui stars

    // ui userRating
    var showRatingDialog by remember { mutableStateOf(false) }
    var userRating by remember { mutableStateOf(5f) }
    // ui userRating

    // ui colors
    val isDark = isSystemInDarkTheme()
    val imageGradColor = if (isDark) BackGroundColor else Color.White
    val textColor = if (isDark) Color.White else Color.Black
    val buttonBackgroundColor =
        if (isDark) BackGroundColorButton else BackGroundColorButtonLightGray
    val buttonTextColor = if (isDark) Color.White else Color.Black
    val buttonBottom = if (isDark) Color.Black else Color.White
    val buttonRate = if (isDark) backColorChatCard else BackGroundColorButtonLightGray
    // ui colors

    LaunchedEffect(id)
    {
        recViewModel.fetchRecommendationsBooks(id, navObject.authors)
    }
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = LocalContentColor.current,
        topBar = {

        },
        bottomBar = {},
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                FloatingActionButton(
                    onClick = { showRatingDialog = true },
                    containerColor = buttonBottom,
                    contentColor = mainColorUiGreen,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
                    Icon(
                        imageVector = if (!isRated) Icons.Default.StarOutline else Icons.Default.Star,
                        contentDescription = "Оценить"
                    )
                }

                FloatingActionButton(
                    onClick = {
                        navData?.let { data ->
                            val book = Book(
                                id = navObject.id,
                                isbn10 = navObject.isbn10,
                                title = navObject.title,
                                description = navObject.description,
                                thumbnail = navObject.thumbnail,
                                authors = navObject.authors.split(", "),
                                publishedDate = navObject.publishedDate,
                                isFavorite = isFavorite,
                                isBookMark = isBookmark,
                                isRated = isRated,
                                userRating = navObject.userRating,
                                publisher = navObject.publisher,
                                pageCount = navObject.pageCount,
                                categories = navObject.categories.split(", "),
                                averageRating = navObject.averageRating,
                                ratingsCount = navObject.ratingsCount,
                                language = navObject.language
                            )
                            onFavsBooks(db, data.uid, book, !book.isFavorite)
                        }
                    },
                    containerColor = buttonBottom,
                    contentColor = mainColorUiGreen,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
                    Icon(
                        imageVector = if (!isFavorite) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                        contentDescription = "В избранное"
                    )
                }

                FloatingActionButton(
                    onClick = {
                        navData?.let { data ->
                            val book = Book(
                                id = navObject.id,
                                isbn10 = navObject.isbn10,
                                title = navObject.title,
                                description = navObject.description,
                                thumbnail = navObject.thumbnail,
                                authors = navObject.authors.split(", "),
                                publishedDate = navObject.publishedDate,
                                isFavorite = isFavorite,
                                isBookMark = isBookmark,
                                isRated = isRated,
                                userRating = navObject.userRating,
                                publisher = navObject.publisher,
                                pageCount = navObject.pageCount,
                                categories = navObject.categories.split(", "),
                                averageRating = navObject.averageRating,
                                ratingsCount = navObject.ratingsCount,
                                language = navObject.language
                            )
                            onBookmarkBooks(db, data.uid, book, !book.isBookMark)
                        }
                    },
                    containerColor = buttonBottom,
                    contentColor = mainColorUiGreen,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (!isBookmark) Icons.Default.BookmarkBorder else Icons.Default.Bookmark,
                            contentDescription = "Прочитать позже"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Прочитать позже",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = custom_font
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(innerPadding),
        ) {
            if (showRatingDialog) {
                Dialog(onDismissRequest = { showRatingDialog = false }) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = buttonBottom,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .wrapContentHeight()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Оцените книгу",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = mainColorUiGreen
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Slider(
                                value = userRating,
                                onValueChange = { userRating = it },
                                valueRange = 1f..10f,
                                steps = 8, // чтобы по 1 шагу (10-1)/9 = 1
                                colors = SliderDefaults.colors(
                                    thumbColor = mainColorUiGreen,
                                    activeTrackColor = mainColorUiGreen
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Вы выбрали: ${userRating.toInt()}",
                                fontSize = 16.sp,
                                color = mainColorUiGreen
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    navData?.let { data ->
                                        val book = Book(
                                            id = navObject.id,
                                            isbn10 = navObject.isbn10,
                                            title = navObject.title,
                                            description = navObject.description,
                                            thumbnail = navObject.thumbnail,
                                            authors = navObject.authors.split(", "),
                                            publishedDate = navObject.publishedDate,
                                            isFavorite = isFavorite,
                                            isBookMark = isBookmark,
                                            isRated = isRated,
                                            userRating = userRating.toInt(),
                                            publisher = navObject.publisher,
                                            pageCount = navObject.pageCount,
                                            categories = navObject.categories.split(", "),
                                            averageRating = navObject.averageRating,
                                            ratingsCount = navObject.ratingsCount,
                                            language = navObject.language
                                        )
                                        onRatedBooks(db, data.uid, book)
                                    }
                                    showRatingDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = buttonRate,
                                    contentColor = mainColorUiGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Поставить оценку",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                ) {
                    AsyncImage(
                        model = navObject.thumbnail.replace("http://", "https://"),
                        contentDescription = "Фон книги",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(20.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        if (isDark) BackGroundColor else Color.White
                                    )
                                )
                            )
                    )
                    AsyncImage(
                        model = navObject.thumbnail.replace("http://", "https://"),
                        contentDescription = "Обложка книги",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .width(200.dp)
                            .height(300.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = navObject.title,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        fontFamily = book_font,
                        textAlign = TextAlign.Center,
                        maxLines = 3, // можно больше
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 64.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = navObject.authors,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = custom_font
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row() {
                        navObject.publisher?.let {
                            if (it.isNotBlank()) {
                                Text(
                                    text = "${it}, ",
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontFamily = custom_font
                                )
                            }
                        }
                        navObject.publishedDate.let {
                            if (it.isNotBlank()) {
                                Text(
                                    text = "${it}, ",
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontFamily = custom_font
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        navObject.pageCount?.takeIf { it > 0 }?.let {
                            Text(
                                text = "${it} cтр.",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                fontFamily = custom_font,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    navObject.language?.let {
                        if (it.isNotBlank()) {
                            val languageName = when (it.lowercase()) {
                                "ru" -> "Русский"
                                "en" -> "Английский"
                                else -> "Иностранный"
                            }
                            Text(
                                text = "Язык: $languageName",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontFamily = custom_font
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = navObject.description,
                            color = textColor,
                            fontSize = 16.sp,
                            fontFamily = custom_font,
                            maxLines = if (expanded) Int.MAX_VALUE else 4,
                            overflow = TextOverflow.Ellipsis
                        )

                        TextButton(onClick = { expanded = !expanded }) {
                            Text(
                                text = if (expanded) "Скрыть" else "Читать далее",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }

                        if (navObject.averageRating != null && navObject.ratingsCount != null) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                navObject.averageRating?.let {
                                    navObject.ratingsCount?.let {
                                        RatingCardBook(
                                            averageRating = navObject.averageRating,
                                            ratingsCount = navObject.ratingsCount,
                                            buttonBackgroundColor = buttonBackgroundColor,
                                            buttonTextColor = buttonTextColor
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Похожие книги",
                            fontSize = 25.sp,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isLoading) {
                                items(3) {
                                    Box(
                                        modifier = Modifier
                                            .width(120.dp)
                                            .height(220.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.height(30.dp),
                                            color = ButtonColor
                                        )
                                    }
                                }
                            } else if (recommendationBooks.isNotEmpty()) {
                                val filteredRecommendations =
                                    recommendationBooks.filter { it.isbn10 != "Неизвестно" }
                                items(filteredRecommendations.size) { index ->
                                    val book = recommendationBooks[index]
                                    Box(
                                        modifier = Modifier
                                            .width(170.dp)
                                            .height(250.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(Color.Gray)
                                            .clickable {
                                                navData.let { data ->
                                                    val detailsNavBookObject = DetailsNavBookObject(
                                                        id = book.id,
                                                        isbn10 = book.isbn10,
                                                        title = book.title,
                                                        authors = book.authors?.joinToString(", ")
                                                            ?: "Неизвестно",
                                                        description = book.description
                                                            ?: "Описание отсутствует",
                                                        thumbnail = book.thumbnail?.replace(
                                                            "http://",
                                                            "https://"
                                                        )
                                                            ?: "",
                                                        publishedDate = book.publishedDate
                                                            ?: "Неизвестно",
                                                        isFavorite = book.isFavorite,
                                                        isBookmark = book.isBookMark,
                                                        isRated = book.isRated,
                                                        userRating = book.userRating,
                                                        publisher = book.publisher,
                                                        pageCount = book.pageCount,
                                                        categories = book.categories?.joinToString(", ")
                                                            ?: "Неизвестно",
                                                        averageRating = book.averageRating,
                                                        ratingsCount = book.ratingsCount,
                                                        language = book.language
                                                    )
                                                    navController.navigate(detailsNavBookObject)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = book.thumbnail?.replace("http://", "https://"),
                                            contentDescription = "Постер фильма",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(250.dp)
                                                .clip(RoundedCornerShape(15.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            } else if (error != null) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Не удалось загрузить рекомендации",
                                            color = Color.Red,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            } else {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Нет рекомендаций для этого фильма",
                                            color = Color.Gray,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 32.dp, start = 8.dp)
                    .align(Alignment.TopStart)
                    .background(
                        color = if (isDark) Color.Black.copy(alpha = 0.4f) else Color.White.copy(
                            alpha = 0.4f
                        ),
                        shape = CircleShape
                    )
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = mainColorUiGreen
                )
            }
        }
        }
    }