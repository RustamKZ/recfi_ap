
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.films_shop.main_screen.objects.auth_screens_objects.AddFriendObject
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.BackGroundColorButton
import com.example.films_shop.ui.theme.BackGroundColorButtonLightGray
import com.example.films_shop.ui.theme.mainColorUiGreen
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.min

@Composable
fun AddFriendWithRequestsScreen(navData: AddFriendObject) {
    val isDark = isSystemInDarkTheme()
    val tabColor = if (isDark) BackGroundColor else Color.White
    val textColor = if (isDark) Color.White else Color.Black
    val indicatorColor = if (isDark) mainColorUiGreen else mainColorUiGreen
    val tabs = listOf("Добавить друга", "Входящие заявки")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = tabColor,
            contentColor = indicatorColor,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = indicatorColor
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            color = if (pagerState.currentPage == index) indicatorColor else textColor
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> AccountAddFriendScreen(navData)
                1 -> IncomingFriendRequestsScreen(navData)
            }
        }
    }
}

@Composable
fun AccountAddFriendScreen(
    navData: AddFriendObject,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val currentUid = navData.uid

    var inputUid by remember { mutableStateOf("") }
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) Color.White else Color.Black
    val buttonBackgroundColor =
        if (isDark) BackGroundColorButton else BackGroundColorButtonLightGray

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(text = "Добавить друга по коду", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = inputUid,
            onValueChange = { inputUid = it },
            label = { Text("Вставьте UID друга") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(buttonBackgroundColor)
                .clickable {
                    inputUid = clipboardManager.getText()?.text ?: ""
                    Toast
                        .makeText(context, "Код вставлен из буфера", Toast.LENGTH_SHORT)
                        .show()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Вставить из буфера обмена",
                color = textColor,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(buttonBackgroundColor)
                .clickable {
                    if (inputUid.isNotBlank() && inputUid != currentUid) {
                        // Сначала проверяем, существует ли такой пользователь
                        db
                            .collection("users")
                            .document(inputUid)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    // Проверяем, не в друзьях ли уже этот UID
                                    db
                                        .collection("users")
                                        .document(currentUid)
                                        .collection("friends")
                                        .document(inputUid)
                                        .get()
                                        .addOnSuccessListener { friendSnapshot ->
                                            if (friendSnapshot.exists()) {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        "Пользователь уже у вас в друзьях",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            } else {
                                                // если все ок — отправляем запрос
                                                val request = hashMapOf(
                                                    "from" to currentUid,
                                                    "status" to "pending",
                                                    "timestamp" to com.google.firebase.Timestamp.now()
                                                )

                                                db
                                                    .collection("users")
                                                    .document(inputUid)
                                                    .collection("friend_requests")
                                                    .document(currentUid)
                                                    .set(request)
                                                    .addOnSuccessListener {
                                                        Toast
                                                            .makeText(
                                                                context,
                                                                "Запрос отправлен!",
                                                                Toast.LENGTH_SHORT
                                                            )
                                                            .show()
                                                        inputUid = ""
                                                    }
                                                    .addOnFailureListener {
                                                        Toast
                                                            .makeText(
                                                                context,
                                                                "Ошибка отправки",
                                                                Toast.LENGTH_SHORT
                                                            )
                                                            .show()
                                                    }
                                            }
                                        }
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            "Пользователь с таким UID не найден",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            }
                            .addOnFailureListener {
                                Toast
                                    .makeText(context, "Ошибка проверки UID", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    } else {
                        Toast
                            .makeText(context, "Невалидный UID", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Отправить приглашение",
                color = textColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun IncomingFriendRequestsScreen(navData: AddFriendObject) {
    val db = FirebaseFirestore.getInstance()
    val currentUid = navData.uid
    val isDark = isSystemInDarkTheme()
    val buttonBackgroundColor =
        if (isDark) BackGroundColorButton else BackGroundColorButtonLightGray
    var requests by remember { mutableStateOf<List<FriendData>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("users")
            .document(currentUid)
            .collection("friend_requests")
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val uids = it.documents.mapNotNull { doc -> doc.getString("from") }

                    uids.forEach { uid ->
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { doc ->
                                val data = FriendData(
                                    uid = uid,
                                    name = doc.getString("name"),
                                    photoUrl = doc.getString("photo")
                                )
                                requests = requests.toMutableList().apply {
                                    removeAll { it.uid == uid }
                                    add(data)
                                }
                            }
                    }
                }
            }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        items(requests, key = { it.uid }) { friend ->
            var offsetX by remember { mutableStateOf(0f) }
            val maxOffset = 1000f
            val backgroundColor = when {
                offsetX > 0 -> mainColorUiGreen.copy(alpha = min(offsetX / maxOffset, 1f))
                offsetX < 0 -> Color.Red.copy(
                    alpha = min(
                        offsetX.absoluteValue / maxOffset,
                        1f
                    )
                )

                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(backgroundColor)
            ) {

                if (offsetX > 50) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept",
                        tint = Color.Green,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp)
                            .scale(min(offsetX / 100f, 1f))
                    )
                }

                if (offsetX < -50) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Reject",
                        tint = Color.Red,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                            .scale(min(offsetX.absoluteValue / 100f, 1f))
                    )
                }

                val dismissed = remember { mutableStateOf(false) }

                if (!dismissed.value) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .offset { IntOffset(offsetX.toInt(), 0) }
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onHorizontalDrag = { _, dragAmount ->
                                        offsetX =
                                            (offsetX + dragAmount).coerceIn(-maxOffset, maxOffset)
                                    },
                                    onDragEnd = {
                                        when {
                                            offsetX >= maxOffset - 350f -> {
                                                val userFriends = db.collection("users")
                                                val batch = db.batch()

                                                val myFriendRef = userFriends
                                                    .document(currentUid)
                                                    .collection("friends")
                                                    .document(friend.uid)
                                                val theirFriendRef =
                                                    userFriends
                                                        .document(friend.uid)
                                                        .collection("friends")
                                                        .document(currentUid)
                                                val requestRef = userFriends
                                                    .document(currentUid)
                                                    .collection("friend_requests")
                                                    .document(friend.uid)

                                                batch.set(
                                                    myFriendRef,
                                                    mapOf("addedAt" to Timestamp.now())
                                                )
                                                batch.set(
                                                    theirFriendRef,
                                                    mapOf("addedAt" to Timestamp.now())
                                                )
                                                batch.update(requestRef, "status", "accepted")

                                                batch
                                                    .commit()
                                                    .addOnSuccessListener {
                                                        dismissed.value = true
                                                        requests =
                                                            requests.filterNot { it.uid == friend.uid }
                                                    }
                                            }

                                            offsetX <= -maxOffset + 350f -> {
                                                db
                                                    .collection("users")
                                                    .document(currentUid)
                                                    .collection("friend_requests")
                                                    .document(friend.uid)
                                                    .update("status", "rejected")
                                                    .addOnSuccessListener {
                                                        dismissed.value = true
                                                        requests =
                                                            requests.filterNot { it.uid == friend.uid }
                                                    }
                                            }

                                            else -> {
                                                offsetX = 0f
                                            }
                                        }
                                    }
                                )
                            }
                            .background(buttonBackgroundColor)
                            .padding(12.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(friend.photoUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = friend.name ?: "Без имени",
                            fontSize = 25.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}
