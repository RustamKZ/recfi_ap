package com.example.films_shop.main_screen.screens.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.films_shop.main_screen.objects.auth_screens_objects.ImageAccountObject
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun AccountImageScreen(
    navData: ImageAccountObject,
    onAvatarSelected: () -> Unit
) {
    val context = LocalContext.current
    val db = remember {
        Firebase.firestore
    }
    val columns = 2
    val avatarUrls = remember { listOf(
        "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/photo1.png",
        "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/photo2.png",
        "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/photo3.png",
        "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/photo4.png",
        "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/photo5.png",
        "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/photo6.png",
        "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/photo7.png",
        "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/photo8.png",
        "https://raw.githubusercontent.com/RustamKZ/recfi_ap/refs/heads/master/photo9.png",
    ) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(avatarUrls) { url ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .clickable {
                        db.collection("users")
                            .document(navData.uid)
                            .update("photo", url)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Аватар успешно выбран", Toast.LENGTH_SHORT).show()
                                onAvatarSelected()
                            }
                    }
            ) {
                AsyncImage( // Coil Compose
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}
