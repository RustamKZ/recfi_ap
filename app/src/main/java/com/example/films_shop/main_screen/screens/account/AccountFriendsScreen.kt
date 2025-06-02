import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.films_shop.main_screen.objects.auth_screens_objects.FriendsAccountObject
import com.google.firebase.firestore.FirebaseFirestore

data class FriendData(
    val uid: String,
    val name: String?,
    val photoUrl: String?
)

@Composable
fun AccountFriendsScreen(
    navData: FriendsAccountObject,
) {
    val db = FirebaseFirestore.getInstance()
    val currentUid = navData.uid

    var friends by remember { mutableStateOf<List<FriendData>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedFriend by remember { mutableStateOf<FriendData?>(null) }
    var removedFriends by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        db.collection("users")
            .document(currentUid)
            .collection("friends")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val friendUids = it.documents.map { doc -> doc.id }
                    friendUids.forEach { friendUid ->
                        db.collection("users")
                            .document(friendUid)
                            .get()
                            .addOnSuccessListener { friendDoc ->
                                val friend = FriendData(
                                    uid = friendUid,
                                    name = friendDoc.getString("name"),
                                    photoUrl = friendDoc.getString("photo")
                                )
                                friends = friends.toMutableList().apply {
                                    removeAll { it.uid == friendUid }
                                    add(friend)
                                }
                            }
                    }
                }
            }
    }

    if (friends.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("У вас пока нет друзей")
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Друзья",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
            }

            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(friends, key = { it.uid }) { friend ->
                    var visible by remember { mutableStateOf(true) }

                    AnimatedVisibility(
                        visible = visible && !removedFriends.contains(friend.uid),
                        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
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
                                IconButton(
                                    onClick = {
                                        selectedFriend = friend
                                        showDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Удалить друга",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог подтверждения удаления
    if (showDialog && selectedFriend != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text("Удалить друга")
            },
            text = {
                Text("Вы действительно хотите удалить друга «${selectedFriend?.name ?: "Без имени"}»?")
            },
            confirmButton = {
                TextButton(onClick = {
                    val friend = selectedFriend!!
                    val userRef = db.collection("users")

                    userRef.document(currentUid)
                        .collection("friends")
                        .document(friend.uid)
                        .delete()
                    userRef.document(friend.uid)
                        .collection("friends")
                        .document(currentUid)
                        .delete()

                    removedFriends = removedFriends + friend.uid
                    friends = friends.filterNot { it.uid == friend.uid }

                    showDialog = false
                }) {
                    Text("Да", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Нет")
                }
            }
        )
    }
}