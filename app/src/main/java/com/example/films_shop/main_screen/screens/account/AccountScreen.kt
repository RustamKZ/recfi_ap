package com.example.films_shop.main_screen.screens.account

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.films_shop.R
import com.example.films_shop.main_screen.bottom_menu.BottomMenu
import com.example.films_shop.main_screen.objects.auth_screens_objects.AccountDetailsObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.AddFriendObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.ChatFriendsObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.FriendsAccountObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.ImageAccountObject
import com.example.films_shop.main_screen.objects.auth_screens_objects.SettingsAccountObject
import com.example.films_shop.main_screen.screens.custom_font
import com.example.films_shop.ui.theme.BackGroundColorButton
import com.example.films_shop.ui.theme.CopyUid
import com.example.films_shop.ui.theme.ExitButtonColor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@Composable
fun AccountScreen(
    navController: NavController,
    navData: AccountDetailsObject,
    showBottomBar: Boolean = true,
    onExitClick: () -> Unit,
) {
    // Добавление в друзья
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    // Добавление в друзья
    val db = Firebase.firestore
    val nameState = remember { mutableStateOf<String?>(null) }
    val photoUrl = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(navData.uid) {
        db.collection("users")
            .document(navData.uid)
            .get()
            .addOnSuccessListener { document ->
                nameState.value = document.getString("name")
            }
        db.collection("users").document(navData.uid).get()
            .addOnSuccessListener { doc ->
                photoUrl.value = doc.getString("photo")
            }
    }
    val scrollState = rememberScrollState()
    val isDark = isSystemInDarkTheme()
    val iconColor = if (isDark) Color.White else BackGroundColorButton
    val composition by rememberLottieComposition(
        spec = if (isDark)
            LottieCompositionSpec.Asset("user_lottie_white.json")
        else
            LottieCompositionSpec.Asset("user_lottie.json")
    )
    val lottieAnimatable = rememberLottieAnimatable()
    LaunchedEffect(composition) {
        composition?.let {
            lottieAnimatable.animate(
                composition = it,
                clipSpec = LottieClipSpec.Progress(0f, 0.5f), // проиграть от 0 до 50%
                speed = 0.5f
            )
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomMenu(
                    navController = navController,
                    uid = navData.uid,
                    email = navData.email
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (photoUrl.value.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clipToBounds()
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = lottieAnimatable.progress,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleY = 1.3f      // немного увеличиваем по вертикали
                                scaleX =
                                    1.3f      // можно и по горизонтали, чтобы не было обрезания краёв
                                translationY = +90f // сдвигаем вверх — подбирай по своему вкусу
                            }
                    )
                }
            } else {
                AsyncImage(
                    model = photoUrl.value,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .size(120.dp)
                        .clip(CircleShape)
                )
            }
            nameState.value?.let { name ->
                if (name.isNotBlank()) {
                    Text(
                        text = name,
                        fontFamily = custom_font,
                        fontSize = 45.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconColor
                    )
                }
            }
            Text(
                text = navData.email,
                fontFamily = custom_font
            )
            Spacer(Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable {
                        navController.navigate(
                            SettingsAccountObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_manage_accounts_24),
                        contentDescription = "Добавить информациию",
                        modifier = Modifier.size(30.dp),
                        tint = iconColor
                    )
                    Spacer(Modifier.width(15.dp))
                    Text(
                        text = "Настройки",
                        fontFamily = custom_font,
                        fontSize = 25.sp,
                        color = iconColor,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = "Посмотреть",
                        tint = iconColor
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable {
                        navController.navigate(
                            ImageAccountObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_face_24),
                        contentDescription = "Добавить информациию",
                        modifier = Modifier.size(30.dp),
                        tint = iconColor
                    )
                    Spacer(Modifier.width(15.dp))
                    Text(
                        text = "Выбрать аватар",
                        fontFamily = custom_font,
                        fontSize = 25.sp,
                        color = iconColor,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = "Посмотреть",
                        tint = iconColor
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable {
                        navController.navigate(
                            AddFriendObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_person_add_alt_1_24),
                        contentDescription = "Добавить друга",
                        modifier = Modifier.size(30.dp),
                        tint = iconColor
                    )
                    Spacer(Modifier.width(15.dp))
                    Text(
                        text = "Добавить друга",
                        fontFamily = custom_font,
                        fontSize = 25.sp,
                        color = iconColor,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = "Посмотреть",
                        tint = iconColor
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable {
                        navController.navigate(
                            FriendsAccountObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_people_24),
                        contentDescription = "Друзья",
                        modifier = Modifier.size(30.dp),
                        tint = iconColor
                    )
                    Spacer(Modifier.width(15.dp))
                    Text(
                        text = "Друзья",
                        fontFamily = custom_font,
                        fontSize = 25.sp,
                        color = iconColor,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = "Посмотреть",
                        tint = iconColor
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable {
                        navController.navigate(
                            ChatFriendsObject(
                                navData.uid,
                                navData.email
                            )
                        )
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_chat_24),
                        contentDescription = "Чат",
                        modifier = Modifier.size(30.dp),
                        tint = iconColor
                    )
                    Spacer(Modifier.width(15.dp))
                    Text(
                        text = "Чат",
                        fontFamily = custom_font,
                        fontSize = 25.sp,
                        color = iconColor,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = "Посмотреть",
                        tint = iconColor
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                contentAlignment = Alignment.CenterStart // Выравнивание Row по левому краю
            ) {
                Row(
                    modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString(navData.uid))
                        Toast.makeText(context, "UID скопирован", Toast.LENGTH_SHORT).show()
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_content_copy_24),
                        contentDescription = "Скопировать UID для добавления в друзья",
                        modifier = Modifier.size(30.dp),
                        tint = CopyUid
                    )
                    Spacer(Modifier.width(15.dp))
                    Text(
                        text = "Код для друзей",
                        fontFamily = custom_font,
                        fontSize = 25.sp,
                        color = CopyUid
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                contentAlignment = Alignment.CenterStart // Выравнивание Row по левому краю
            ) {
                Row(
                    modifier = Modifier.clickable {
                        onExitClick()
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_logout_24),
                        contentDescription = "Выйти",
                        modifier = Modifier.size(30.dp),
                        tint = ExitButtonColor
                    )
                    Spacer(Modifier.width(15.dp))
                    Text(
                        text = "Выйти",
                        fontFamily = custom_font,
                        fontSize = 25.sp,
                        color = ExitButtonColor
                    )
                }
            }
        }
    }
}