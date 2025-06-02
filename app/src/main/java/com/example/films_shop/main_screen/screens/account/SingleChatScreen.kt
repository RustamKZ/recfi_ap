package com.example.films_shop.main_screen.screens.account

import MovieViewModel
import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.films_shop.main_screen.api.BookApi.Book
import com.example.films_shop.main_screen.api.BookApi.BookViewModel
import com.example.films_shop.main_screen.api.BookApi.RetrofitInstanceBooks
import com.example.films_shop.main_screen.api.Movie
import com.example.films_shop.main_screen.api.RetrofitInstance
import com.example.films_shop.main_screen.api.apiKey
import com.example.films_shop.main_screen.api.apiKeyBook
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavBookObject
import com.example.films_shop.main_screen.objects.details_screens_objects.DetailsNavMovieObject
import com.example.films_shop.ui.theme.BackGroundColorButton
import com.example.films_shop.ui.theme.BackGroundColorButtonLightGray
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import retrofit2.HttpException

@Parcelize
data class ChatMessage(
    val from: String = "",
    val to: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val read: Boolean = false,
    val content: Boolean = false,
    val imageUrl: String = "",
    val idContent: String = "",
    val rating: String = "",
    val isBook: Boolean = false
    // фильмы

) : Parcelable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChatScreen(
    navController: NavController,
    movieViewModel: MovieViewModel,
    bookViewModel: BookViewModel,
    uid: String,
    friendUid: String,
    friendName: String,
    friendPhotoUrl: String?,
) {
    val coroutineScope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) Color.White else Color.Black
    val buttonBackgroundColor =
        if (isDark) BackGroundColorButton else BackGroundColorButtonLightGray
    val db = FirebaseFirestore.getInstance()
    val currentUid = uid
    val chatId = listOf(currentUid, friendUid).sorted().joinToString("_")

    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    val focusManager = LocalFocusManager.current

    var isTyping by remember { mutableStateOf(false) }
    var friendTyping by remember { mutableStateOf(false) }
    var typingTimerJob by remember { mutableStateOf<Job?>(null) }

    // скрепка
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showSheet = remember { mutableStateOf(false) }


    LaunchedEffect(chatId) {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    messages = it.documents.mapNotNull { doc ->
                        doc.toObject(ChatMessage::class.java)
                    }
                }
            }
    }
    LaunchedEffect(friendUid) {
        db.collection("chats")
            .document(chatId)
            .collection("typingStatus")
            .document(friendUid)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.getBoolean("typing")?.let {
                    friendTyping = it
                }
            }
    }
    val showMediaSheet = remember { mutableStateOf(false) }
    val isBookMode = remember { mutableStateOf(false) }

    if (showMediaSheet.value) {
        MediaSelectionBottomSheet(
            onDismiss = { showMediaSheet.value = false },
            isBook = isBookMode.value,
            currentUid = currentUid,
            friendUid = friendUid,
            chatId = chatId,
            db = db,
            movieViewModel = movieViewModel,
            bookViewModel = bookViewModel
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!friendPhotoUrl.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(friendPhotoUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(friendName, fontSize = 30.sp)
                    Spacer(Modifier.width(8.dp))
                    if (friendTyping) {
                        Text(
                            "печатает...",
                            fontSize = 30.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
            reverseLayout = true
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(top = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Добро пожаловать в чат", fontSize = 18.sp)
                    }
                }
            } else {
                items(messages.reversed()) { message ->
                    val isMe = message.from == currentUid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                tonalElevation = 4.dp
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    if (message.content) {
                                        Log.d("Chat", "Sent content")
                                        ContentCardPlaceholder(
                                            contentId = message.idContent,
                                            imageUrl = message.imageUrl,
                                            title = message.text,
                                            rating = message.rating,
                                            isBook = message.isBook,
                                            navController = navController,
                                            bookViewModel = bookViewModel,
                                            movieViewModel = movieViewModel
                                        )
                                    } else {
                                        Log.d("Chat", "Sent message ${message.content}")
                                        Text(
                                            text = message.text,
                                            color = Color.White,
                                            fontSize = 20.sp
                                        )
                                    }


                                    if (isMe && message.read) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Прочитано",
                                            fontSize = 10.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Divider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                showSheet.value = true
            }) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Прикрепить файл"
                )
            }

            OutlinedTextField(
                value = messageText,
                onValueChange = { text ->
                    messageText = text

                    if (!isTyping) {
                        isTyping = true
                        db.collection("chats")
                            .document(chatId)
                            .collection("typingStatus")
                            .document(currentUid)
                            .set(mapOf("typing" to true))
                    }

                    // Перезапускаем таймер
                    typingTimerJob?.cancel()
                    typingTimerJob = coroutineScope.launch {
                        delay(2000L)
                        isTyping = false
                        db.collection("chats")
                            .document(chatId)
                            .collection("typingStatus")
                            .document(currentUid)
                            .set(mapOf("typing" to false))
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Введите сообщение") },
                maxLines = 3,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (messageText.isNotBlank()) {
                            sendMessage(currentUid, friendUid, chatId, messageText)
                            messageText = ""
                            focusManager.clearFocus()
                        }
                    }
                )
            )

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        sendMessage(currentUid, friendUid, chatId, messageText)
                        messageText = ""
                        focusManager.clearFocus()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Отправить",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    if (showSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showSheet.value = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Выберите тип вложения", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showSheet.value = false
                        isBookMode.value = false
                        showMediaSheet.value = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(buttonBackgroundColor)
                ) {
                    Text("Фильмы", color = textColor)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        showSheet.value = false
                        isBookMode.value = true
                        showMediaSheet.value = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(buttonBackgroundColor)
                ) {
                    Text("Книги", color = textColor)
                }
            }
        }
    }

}

fun sendMessage(from: String, to: String, chatId: String, text: String) {
    val db = FirebaseFirestore.getInstance()
    val message = ChatMessage(
        from = from,
        to = to,
        text = text,
        timestamp = System.currentTimeMillis(),
        read = false
    )
    db.collection("chats")
        .document(chatId)
        .collection("messages")
        .add(message)
}

fun sendContentMessage(
    from: String,
    to: String,
    chatId: String,
    contentId: String,
    text: String,
    imageUrl: String,
    rating: String,
    isBook: Boolean
) {
    val db = FirebaseFirestore.getInstance()
    val message = ChatMessage(
        from = from,
        to = to,
        text = text,
        timestamp = System.currentTimeMillis(),
        read = false,
        content = true,
        imageUrl = imageUrl,
        idContent = contentId,
        rating = rating,
        isBook = isBook
    )
    db.collection("chats")
        .document(chatId)
        .collection("messages")
        .add(message)
}

@Composable
fun MediaCardRow(
    imageUrl: String,
    title: String,
    rating: String? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 18.sp)
        }
        if (rating != null) {
            Text(text = rating, fontSize = 16.sp, color = Color.Gray)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSelectionBottomSheet(
    onDismiss: () -> Unit,
    isBook: Boolean,
    currentUid: String = "",
    friendUid: String = "",
    chatId: String = "",
    db: FirebaseFirestore,
    movieViewModel: MovieViewModel,
    bookViewModel: BookViewModel,
) {
    LaunchedEffect(Unit) {
        movieViewModel.loadFavoriteMovies(db, currentUid, ContentType.MOVIES)
        movieViewModel.loadFavoriteMovies(db, currentUid, ContentType.TV_SERIES)
        movieViewModel.loadFavoriteMovies(db, currentUid, ContentType.CARTOONS)

        movieViewModel.loadBookmarkMovies(db, currentUid, ContentType.MOVIES)
        movieViewModel.loadBookmarkMovies(db, currentUid, ContentType.TV_SERIES)
        movieViewModel.loadFavoriteMovies(db, currentUid, ContentType.CARTOONS)

        movieViewModel.loadRatedMovies(db, currentUid, ContentType.MOVIES)
        movieViewModel.loadRatedMovies(db, currentUid, ContentType.TV_SERIES)
        movieViewModel.loadRatedMovies(db, currentUid, ContentType.CARTOONS)

        bookViewModel.loadBookmarkBooks(db, currentUid)
        bookViewModel.loadFavoriteBooks(db, currentUid)
        bookViewModel.loadRatedBooks(db, currentUid)
    }
    val ratedMovies = remember { movieViewModel.ratedMoviesState }
    val ratedTvSeries = remember { movieViewModel.ratedTvSeriesState }
    val ratedCartoons = remember { movieViewModel.ratedCartoonsState }

    val favMovies = remember { movieViewModel.favoriteMoviesState }
    val favTvSeries = remember { movieViewModel.favoriteTvSeriesState }
    val favCartoons = remember { movieViewModel.favoriteCartoonsState }

    val bookmarkMovies = remember { movieViewModel.bookmarkMoviesState }
    val bookmarkTvSeries = remember { movieViewModel.bookmarkTvSeriesState }
    val bookmarkCartoons = remember { movieViewModel.bookmarkCartoonsState }

    val favBooks = remember { bookViewModel.favoriteBooksState }
    val ratedBooks = remember { bookViewModel.ratedBooksState }
    val bookmarkBooks = remember { bookViewModel.bookmarkBooksState }

    val movies = combineAndDeduplicateMovies(
        ratedMovies.value,
        favMovies.value,
        bookmarkMovies.value
    )

    val tvSeries = combineAndDeduplicateMovies(
        ratedTvSeries.value,
        favTvSeries.value,
        bookmarkTvSeries.value
    )

    val cartoons = combineAndDeduplicateMovies(
        ratedCartoons.value,
        favCartoons.value,
        bookmarkCartoons.value
    )

    val books = combineAndDeduplicateBooks(
        ratedBooks.value,
        favBooks.value,
        bookmarkBooks.value
    )


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!isBook) {
                Text("Фильмы", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
                movies.forEach { (movie, rating) ->
                    MediaCardRow(
                        imageUrl = movie.poster!!.url ?: "",
                        title = movie.name ?: "",
                        rating = rating?.let { "⭐ $it" } ?: "",
                        onClick = {
                            onDismiss()
                            sendContentMessage(
                                from = currentUid,
                                to = friendUid,
                                chatId = chatId,
                                contentId = movie.externalId?.tmdb.toString(),
                                imageUrl = movie.poster.url,
                                text = movie.name ?: "",
                                rating = rating.toString(),
                                isBook = false
                            )
                        }
                    )
                }

                Text("Сериалы", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
                tvSeries.forEach { (movie, rating) ->
                    MediaCardRow(
                        imageUrl = movie.poster!!.url ?: "",
                        title = movie.name ?: "",
                        rating = rating?.let { "⭐ $it" } ?: "",
                        onClick = {
                            onDismiss()
                            sendContentMessage(
                                from = currentUid,
                                to = friendUid,
                                chatId = chatId,
                                contentId = movie.externalId?.tmdb.toString(),
                                imageUrl = movie.poster.url,
                                text = movie.name ?: "",
                                rating = rating.toString(),
                                isBook = false
                            )
                        }
                    )
                }

                Text("Мультфильмы", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
                cartoons.forEach { (movie, rating) ->
                    MediaCardRow(
                        imageUrl = movie.poster!!.url ?: "",
                        title = movie.name ?: "",
                        rating = rating?.let { "⭐ $it" } ?: "",
                        onClick = {
                            onDismiss()
                            sendContentMessage(
                                from = currentUid,
                                to = friendUid,
                                chatId = chatId,
                                contentId = movie.externalId?.tmdb.toString(),
                                imageUrl = movie.poster.url,
                                text = movie.name ?: "",
                                rating = rating.toString(),
                                isBook = false
                            )
                        }
                    )
                }
            } else {
                Text("Книги", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
                books.forEach { (book, rating) ->
                    MediaCardRow(
                        imageUrl = book.thumbnail ?: "",
                        title = book.title ?: "",
                        rating = rating?.let { "⭐ $it" } ?: "",
                        onClick = {
                            onDismiss()
                            sendContentMessage(
                                from = currentUid,
                                to = friendUid,
                                chatId = chatId,
                                contentId = book.isbn10,
                                imageUrl = book.thumbnail ?: "",
                                text = book.title,
                                rating = rating.toString(),
                                isBook = true
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ContentCardPlaceholder(
    contentId: String,
    imageUrl: String,
    title: String,
    rating: String,
    isBook: Boolean,
    navController: NavController,
    bookViewModel: BookViewModel,
    movieViewModel: MovieViewModel,
) {
    // Заглушка — можно будет заменить на реальный fetch из Firestore
    val imageUrl = imageUrl
    val title = title
    val rating = "$rating⭐"
    val id = contentId
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp)
            .clickable {
                coroutineScope.launch {
                    Log.d("BookSearch", "Entering book search block $id")
                    if (isBook)
                    {
                        Log.d("BookSearch", "Searching for ISBN: $id")
                        try {
                            Log.d("BookSearch", "Запрос к Google Books: isbn:$id")
                            val response = RetrofitInstanceBooks.api.searchBookByIsbn(
                                isbnQuery = "isbn:$id",
                                apiKey = apiKeyBook
                            )
                            Log.d("BookSearch", "Успешно получен ответ: $response")
                            val bookItem = response.items?.firstOrNull()
                            if (bookItem != null) {
                                val volume = bookItem.volumeInfo
                                val isbn10 = volume.industryIdentifiers
                                    ?.firstOrNull { it.type == "ISBN_10" }
                                    ?.identifier ?: "Неизвестно"

                                val thumbnail = volume.imageLinks?.thumbnail
                                    ?: "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/poster.jpg"

                                navController.navigate(
                                    DetailsNavBookObject(
                                        id = bookItem.id,
                                        isbn10 = isbn10,
                                        title = volume.title,
                                        authors = volume.authors?.joinToString(", ") ?: "Неизвестно",
                                        description = volume.description ?: "Описание отсутствует",
                                        thumbnail = thumbnail,
                                        publishedDate = volume.publishedDate ?: "Неизвестно",
                                        isFavorite = bookViewModel.isInFavorites(bookItem.id),
                                        isBookmark = bookViewModel.isInBookmarks(bookItem.id),
                                        isRated = bookViewModel.isInRated(bookItem.id),
                                        userRating = rating.toInt(),
                                        publisher = volume.publisher,
                                        pageCount = volume.pageCount,
                                        categories = volume.categories?.joinToString(", ")
                                            ?: "Неизвестно",
                                        averageRating = volume.averageRating,
                                        ratingsCount = volume.ratingsCount,
                                        language = volume.language
                                    )
                                )
                            }
                        } catch (e: HttpException) {
                            Log.e("BookSearch", "HttpException: ${e.code()} ${e.message()}", e)
                        } catch (e: Exception) {
                            Log.e("BookSearch", "Иная ошибка: ${e.message}", e)
                        }
                    }
                    else {
                        val response = RetrofitInstance.api.getMovieByTmdbId(
                            apiKey = apiKey,
                            id = id
                        )
                        val movie = response.docs.firstOrNull()
                        movie?.let { data ->
                            val detailsNavObject = DetailsNavMovieObject(
                                id = movie.id,
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
                                persons = movie.persons
                                    ?.filter { it.profession == "режиссеры" }
                                    ?.joinToString(", ") { "${it.name}|${it.photo}" }
                                    ?: "Неизвестно",
                                isFavorite = movieViewModel.isInFavorites(
                                    movie.id
                                ),
                                isBookMark = movieViewModel.isInBookmarks(
                                    movie.id
                                ),
                                isRated = movieViewModel.isInRated(
                                    movie.id
                                ),
                                userRating = movie.userRating ?: 0
                            )
                            navController.navigate(detailsNavObject)
                        }
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = rating, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

// Вспомогательная функция для объединения коллекций медиа контента
fun combineAndDeduplicateMovies(
    rated: List<Movie>,
    favorite: List<Movie>,
    bookmarked: List<Movie>,
): List<Pair<Movie, Int?>> {
    val ratedMap =
        rated.associateBy { it.externalId?.tmdb } // ключ: tmdbId, значение: Movie (с оценкой)
    val combined = (rated + favorite + bookmarked)
        .distinctBy { it.externalId?.tmdb } // убираем дубликаты
        .map { movie ->
            val rating = ratedMap[movie.externalId?.tmdb]?.userRating // если есть, берём оценку
            movie to rating
        }
    return combined
}

fun combineAndDeduplicateBooks(
    rated: List<Book>,
    favorite: List<Book>,
    bookmarked: List<Book>,
): List<Pair<Book, Int?>> {
    val ratedMap = rated.associateBy { it.isbn10 }
    val combined = (rated + favorite + bookmarked)
        .distinctBy { it.isbn10 }
        .map { book ->
            val rating = ratedMap[book.isbn10]?.userRating
            book to rating
        }
    return combined
}




