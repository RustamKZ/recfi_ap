package com.example.films_shop.main_screen.screens.account

import FriendData
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.films_shop.main_screen.objects.auth_screens_objects.ChatFriendsObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.SingleChatScreenDestination
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ChatScreen(
    navController: NavController,
    navData: ChatFriendsObject
) {
    val db = FirebaseFirestore.getInstance()
    val currentUid = navData.uid

    var friends by remember { mutableStateOf<List<FriendData>>(emptyList()) }

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
                    text = "Чаты",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
            }

            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(friends, key = { it.uid }) { friend ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                navController.navigate(
                                    SingleChatScreenDestination(
                                        uid = navData.uid,
                                        friendUid = friend.uid,
                                        friendName = friend.name ?: "Без имени",
                                        photoUrl = friend.photoUrl ?: "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/no_photo.png",
                                    )
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
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
}
