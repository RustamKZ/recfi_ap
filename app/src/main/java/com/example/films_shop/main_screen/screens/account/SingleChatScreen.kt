package com.example.films_shop.main_screen.screens.account

import MovieViewModel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.films_shop.main_screen.Genres.genres
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
import com.example.films_shop.ui.theme.BackGroundColorChatCardDarkGray
import com.example.films_shop.ui.theme.BackGroundColorChatDarkGray
import com.example.films_shop.ui.theme.backColorChatCard
import com.example.films_shop.ui.theme.mainColorUiGreen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.parcelize.Parcelize
import retrofit2.HttpException

@Parcelize
data class ChatMessage(
    val chatId: String = "",
    val id_message: String = "",
    val from: String = "",
    val to: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val read: Boolean = false,
    val content: Boolean = false,
    val imageUrl: String = "",
    val idContent: String = "",
    val rating: String = "",
    val id: String = "",
    val genres: String = "",
    val year: String = "",
    val backdrop: String = "",
    val type: String = "",
) : Parcelable

//@Composable
//fun ShakingCard(
//    isMe: Boolean,
//    content: @Composable () -> Unit
//) {
//    val infiniteTransition = rememberInfiniteTransition(label = "")
//    val offsetX by infiniteTransition.animateFloat(
//        initialValue = -2f,
//        targetValue = 2f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(100),
//            repeatMode = RepeatMode.Reverse
//        ), label = ""
//    )
//
//    Box(
//        modifier = Modifier
//            .offset(x = offsetX.dp)
//    ) {
//        content()
//    }
//}


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
    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        )
    )
    val coroutineScope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) Color.White else Color.Black
    val buttonBackgroundColor =
        if (isDark) backColorChatCard else Color.White
    val db = FirebaseFirestore.getInstance()
    val currentUid = uid
    val chatId = listOf(currentUid, friendUid).sorted().joinToString("_")

    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    val focusManager = LocalFocusManager.current

    var isTyping by remember { mutableStateOf(false) }
    var friendTyping by remember { mutableStateOf(false) }
    var typingTimerJob by remember { mutableStateOf<Job?>(null) }
    val showSheet = remember { mutableStateOf(false) }
    val customTextFieldColors = TextFieldColors(
        focusedTextColor = if (isDark) Color.White else Color.Black,
        unfocusedTextColor = if (isDark) Color.White else Color.Black,
        disabledTextColor = Color.Gray,
        errorTextColor = Color.Red,

        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,

        cursorColor = mainColorUiGreen,
        errorCursorColor = Color.Red,
        textSelectionColors = TextSelectionColors(
            handleColor = mainColorUiGreen,
            backgroundColor = mainColorUiGreen.copy(alpha = 0.4f)
        ),

        focusedIndicatorColor = mainColorUiGreen,
        unfocusedIndicatorColor = Color.Gray,
        disabledIndicatorColor = Color.LightGray,
        errorIndicatorColor = Color.Red,

        focusedLeadingIconColor = Color.Gray,
        unfocusedLeadingIconColor = Color.Gray,
        disabledLeadingIconColor = Color.LightGray,
        errorLeadingIconColor = Color.Red,

        focusedTrailingIconColor = Color.Gray,
        unfocusedTrailingIconColor = Color.Gray,
        disabledTrailingIconColor = Color.LightGray,
        errorTrailingIconColor = Color.Red,

        focusedLabelColor = mainColorUiGreen,
        unfocusedLabelColor = Color.Gray,
        disabledLabelColor = Color.LightGray,
        errorLabelColor = Color.Red,

        focusedPlaceholderColor = Color.Gray,
        unfocusedPlaceholderColor = Color.Gray,
        disabledPlaceholderColor = Color.LightGray,
        errorPlaceholderColor = Color.Red,

        focusedSupportingTextColor = Color.Transparent,
        unfocusedSupportingTextColor = Color.Transparent,
        disabledSupportingTextColor = Color.Transparent,
        errorSupportingTextColor = Color.Red,

        focusedPrefixColor = Color.Gray,
        unfocusedPrefixColor = Color.Gray,
        disabledPrefixColor = Color.LightGray,
        errorPrefixColor = Color.Red,

        focusedSuffixColor = Color.Gray,
        unfocusedSuffixColor = Color.Gray,
        disabledSuffixColor = Color.LightGray,
        errorSuffixColor = Color.Red
    )

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
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    if (message.content) {
                                        if (isMe) {
                                            ContentCardPlaceholder(
                                                contentId = message.idContent,
                                                imageUrl = message.imageUrl,
                                                title = message.text,
                                                rating = message.rating,
                                                navController = navController,
                                                movieViewModel = movieViewModel,
                                                textColor = textColor,
                                                buttonBackgroundColor = buttonBackgroundColor,
                                                genres = message.genres,
                                                year = message.year,
                                                backdrop = message.backdrop,
                                                type = message.type,
                                                isMe = isMe,
                                                chatId = message.chatId,
                                                messageId = message.id_message,
                                                db = db
                                            )
                                        } else if (message.read) {
                                            ContentCardPlaceholder(
                                                contentId = message.idContent,
                                                imageUrl = message.imageUrl,
                                                title = message.text,
                                                rating = message.rating,
                                                navController = navController,
                                                movieViewModel = movieViewModel,
                                                textColor = textColor,
                                                buttonBackgroundColor = buttonBackgroundColor,
                                                genres = message.genres,
                                                year = message.year,
                                                backdrop = message.backdrop,
                                                type = message.type,
                                                isMe = isMe,
                                                chatId = message.chatId,
                                                messageId = message.id_message,
                                                db = db
                                            )
                                        } else {
                                            Box(modifier = Modifier.scale(scale)) {
                                                ContentCardPlaceholder(
                                                    contentId = message.idContent,
                                                    imageUrl = message.imageUrl,
                                                    title = message.text,
                                                    rating = message.rating,
                                                    navController = navController,
                                                    movieViewModel = movieViewModel,
                                                    textColor = textColor,
                                                    buttonBackgroundColor = buttonBackgroundColor,
                                                    genres = message.genres,
                                                    year = message.year,
                                                    backdrop = message.backdrop,
                                                    type = message.type,
                                                    isMe = isMe,
                                                    chatId = message.chatId,
                                                    messageId = message.id_message,
                                                    db = db
                                                )
                                            }
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .then(
                                                    if (isMe) {
                                                        if (isDark) {
                                                            Modifier.background(
                                                                backColorChatCard,
                                                                shape = RoundedCornerShape(30.dp)
                                                            )
                                                        } else {
                                                            Modifier.background(
                                                                BackGroundColorChatDarkGray,
                                                                shape = RoundedCornerShape(30.dp)
                                                            )
                                                        }
                                                    } else {
                                                        Modifier.background(
                                                            mainColorUiGreen,
                                                            shape = RoundedCornerShape(30.dp)
                                                        )
                                                    }
                                                )
                                        ) {
                                            Text(
                                                text = message.text,
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                modifier = Modifier.padding(15.dp)
                                            )
                                        }
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
                    contentDescription = "Прикрепить файл",
                    tint = mainColorUiGreen
                )
            }

            OutlinedTextField(
                colors = customTextFieldColors,
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
                    tint = mainColorUiGreen
                )
            }
        }
    }
    if (showSheet.value) {
        showSheet.value = false
        isBookMode.value = false
        showMediaSheet.value = true
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
    id: String = "",
    genres: String = "",
    year: String = "",
    backdrop: String = "",
    type: String = "",
    onSuccess: ((String) -> Unit)? = null, // Колбэк для получения messageId
    onFailure: ((Exception) -> Unit)? = null,
) {
    val db = FirebaseFirestore.getInstance()
    val message = ChatMessage(
        chatId = chatId,
        id_message = "",
        from = from,
        to = to,
        text = text,
        timestamp = System.currentTimeMillis(),
        read = false,
        content = true,
        imageUrl = imageUrl,
        idContent = contentId,
        rating = rating,
        id = id,
        genres = genres,
        year = year,
        backdrop = backdrop,
        type = type
    )

    db.collection("chats")
        .document(chatId)
        .collection("messages")
        .add(message)
        .addOnSuccessListener { documentReference ->
            val generatedId = documentReference.id

            // Обновляем поле id в документе
            documentReference.update("id_message", generatedId)
                .addOnSuccessListener {
                    onSuccess?.invoke(generatedId)
                }
                .addOnFailureListener { e ->
                    onFailure?.invoke(e)
                }
        }
        .addOnFailureListener { e ->
            onFailure?.invoke(e)
        }
}


@Composable
fun MediaCardRow(
    imageUrl: String,
    title: String,
    rating: String? = null,
    genres: String,
    year: String,
    type: String,
    onClick: () -> Unit = {},
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) Color.White else Color.Black
    val type = when (type) {
        "movie" -> "Фильм"
        "tv-series" -> "Сериал"
        "cartoon" -> "Мультфильм"
        else -> "Неизвестно"
    }
    val genres_ch = genres.split(",")
        .mapIndexed { index, genre ->
            if (index == 0) genre.trim().replaceFirstChar { it.uppercaseChar() }
            else genre.trim()
        }
        .joinToString(", ")

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
                .size(width = 90.dp, height = 60.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "$genres_ch · $year · $type",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(end = 20.dp)
            )
        }
        if (rating != "") {
            Row() {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Отправить",
                    tint = mainColorUiGreen
                )
                Text(
                    text = rating ?: "",
                    fontSize = 25.sp,
                    color = mainColorUiGreen,
                    modifier = Modifier.padding(start = 2.dp, end = 5.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSelectionBottomSheet(
    onDismiss: () -> Unit,
    currentUid: String = "",
    friendUid: String = "",
    chatId: String = "",
    db: FirebaseFirestore,
    movieViewModel: MovieViewModel,
    bookViewModel: BookViewModel,
) {
    val scrollState = rememberScrollState()
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


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                "Фильмы",
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )
            movies.forEach { (movie, rating) ->
                MediaCardRow(
                    imageUrl = movie.backdrop?.url ?: "",
                    title = movie.name ?: "",
                    rating = rating?.let { "$it" } ?: "",
                    genres = movie.genres?.joinToString(", ") { it.name }
                        ?: "Неизвестно",
                    year = movie.year ?: "",
                    type = movie.type ?: "",
                    onClick = {
                        onDismiss()
                        sendContentMessage(
                            from = currentUid,
                            to = friendUid,
                            chatId = chatId,
                            contentId = movie.externalId?.tmdb.toString(),
                            imageUrl = movie.poster?.url ?: "",
                            text = movie.name ?: "",
                            rating = rating.toString(),
                            genres = movie.genres?.joinToString(", ") { it.name }
                                ?: "Неизвестно",
                            year = movie.year ?: "",
                            backdrop = movie.backdrop?.url ?: "",
                            type = when (movie.type) {
                                "movie" -> "Фильм"
                                "tv-series" -> "Сериал"
                                "cartoon" -> "Мультфильм"
                                else -> "Неизвестно"
                            },
                            onSuccess = { messageId ->
                                Log.d("Firestore", "ID сообщения: $messageId")
                            },
                            onFailure = { e ->
                                Log.e("Firestore", "Ошибка при отправке", e)
                            }
                        )
                    }
                )
            }

            Text(
                "Сериалы",
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )
            tvSeries.forEach { (movie, rating) ->
                MediaCardRow(
                    imageUrl = movie.backdrop?.url ?: "",
                    title = movie.name ?: "",
                    rating = rating?.let { "$it" } ?: "",
                    genres = movie.genres?.joinToString(", ") { it.name }
                        ?: "Неизвестно",
                    year = movie.year ?: "",
                    type = movie.type ?: "",
                    onClick = {
                        onDismiss()
                        sendContentMessage(
                            from = currentUid,
                            to = friendUid,
                            chatId = chatId,
                            contentId = movie.externalId?.tmdb.toString(),
                            imageUrl = movie.poster?.url ?: "",
                            text = movie.name ?: "",
                            rating = rating.toString(),
                            genres = movie.genres?.joinToString(", ") { it.name }
                                ?: "Неизвестно",
                            year = movie.year ?: "",
                            backdrop = movie.backdrop?.url ?: "",
                            type = when (movie.type) {
                                "movie" -> "Фильм"
                                "tv-series" -> "Сериал"
                                "cartoon" -> "Мультфильм"
                                else -> "Неизвестно"
                            },
                        )
                    }
                )
            }

            Text(
                "Мультфильмы",
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )
            cartoons.forEach { (movie, rating) ->
                MediaCardRow(
                    imageUrl = movie.backdrop?.url ?: "",
                    title = movie.name ?: "",
                    rating = rating?.let { "$it" } ?: "",
                    genres = movie.genres?.joinToString(", ") { it.name }
                        ?: "Неизвестно",
                    year = movie.year ?: "",
                    type = movie.type ?: "",
                    onClick = {
                        onDismiss()
                        sendContentMessage(
                            from = currentUid,
                            to = friendUid,
                            chatId = chatId,
                            contentId = movie.externalId?.tmdb.toString(),
                            imageUrl = movie.poster?.url ?: "",
                            text = movie.name ?: "",
                            rating = rating.toString(),
                            genres = movie.genres?.joinToString(", ") { it.name }
                                ?: "Неизвестно",
                            year = movie.year ?: "",
                            backdrop = movie.backdrop?.url ?: "",
                            type = when (movie.type) {
                                "movie" -> "Фильм"
                                "tv-series" -> "Сериал"
                                "cartoon" -> "Мультфильм"
                                else -> "Неизвестно"
                            },
                        )
                    }
                )
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
    navController: NavController,
    movieViewModel: MovieViewModel,
    textColor: Color,
    buttonBackgroundColor: Color,
    genres: String,
    year: String,
    backdrop: String,
    type: String,
    isMe: Boolean,
    chatId: String,
    messageId: String,
    db: FirebaseFirestore,
) {
    val id = contentId
    val coroutineScope = rememberCoroutineScope()
    var isClicked by remember { mutableStateOf(false) }

    LaunchedEffect(isClicked) {
        if (isClicked) {
            try {
                val response = RetrofitInstance.api.getMovieByTmdbId(
                    apiKey = apiKey,
                    id = contentId
                )
                val movie = response.docs.firstOrNull()
                movie?.let { movie ->
                    val detailsNavObject = DetailsNavMovieObject(
                        id = movie.id,
                        tmdbId = movie.externalId?.tmdb ?: 0,
                        title = movie.name ?: "Неизвестно",
                        type = movie.type ?: "Неизвестно",
                        genre = movie.genres?.joinToString(", ") { it.name }
                            ?: "Неизвестно",
                        year = movie.year ?: "Неизвестно",
                        description = movie.description ?: "Описание отсутствует",
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
                        isFavorite = movieViewModel.isInFavorites(movie.id),
                        isBookMark = movieViewModel.isInBookmarks(movie.id),
                        isRated = movieViewModel.isInRated(movie.id),
                        userRating = movie.userRating ?: 0
                    )
                    navController.navigate(detailsNavObject)
                }
                if (!isMe) {
                    val messageRef = db
                        .collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .document(messageId)

                    messageRef.update("read", true).await()
                }
            } catch (e: Exception) {
                Log.e("Clickable", "Ошибка при переходе или обновлении read", e)
            } finally {
                isClicked = false
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isMe) {
                    Modifier
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = mainColorUiGreen,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            )
            .background(buttonBackgroundColor, shape = RoundedCornerShape(8.dp))
            .clickable {
                isClicked = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(backdrop),
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
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "$genres · $year · $type",
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




