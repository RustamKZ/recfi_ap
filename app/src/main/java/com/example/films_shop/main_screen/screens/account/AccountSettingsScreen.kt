package com.example.films_shop.main_screen.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.films_shop.main_screen.business_logic.addOrChangeName
import com.example.films_shop.main_screen.login.LoginButton
import com.example.films_shop.main_screen.login.RoundedCornerTextField
import com.example.films_shop.main_screen.objects.auth_screens_objects.SettingsAccountObject
import com.example.films_shop.main_screen.screens.custom_font
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.BackGroundColorButton
import com.example.films_shop.ui.theme.BackGroundColorButtonLightGray
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun AccountSettingsScreen(
    navData: SettingsAccountObject,
) {
    val successMessageState = remember { mutableStateOf("") }
    val db = remember {
        Firebase.firestore
    }
    val isDark = isSystemInDarkTheme()
    val backColor = if (isDark) BackGroundColor else Color.White
    val textColor = if (isDark) Color.White else BackGroundColor
    val backColorTextField = if (isDark) BackGroundColorButton else BackGroundColorButtonLightGray
    val auth = remember {
        Firebase.auth
    }
    val nameState = remember { mutableStateOf("") }
    val errorState = remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backColor)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 50.dp
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Вы можете изменить имя",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = custom_font,
            color = textColor,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(15.dp))
        RoundedCornerTextField(
            text = nameState.value,
            label = "Имя",
            backColorTextField = backColorTextField
        )
        {
            nameState.value = it
        }
        Spacer(Modifier.height(15.dp))
        LoginButton("Изменить", backColor, textColor) {
            addOrChangeName(db, navData.uid, nameState.value) { isSuccess ->
                if (isSuccess) {
                    successMessageState.value = "Имя успешно обновлено"
                } else {
                    successMessageState.value = "Ошибка при обновлении имени"
                }
            }
        }
        if (successMessageState.value.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = successMessageState.value,
                color = if (successMessageState.value.contains("ошибка", ignoreCase = true)) Color.Red else Color.Green,
                fontSize = 16.sp,
                fontFamily = custom_font,
                textAlign = TextAlign.Center
            )
        }


    }
}