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
import com.example.films_shop.main_screen.login.LoginButton
import com.example.films_shop.main_screen.login.RoundedCornerTextField
import com.example.films_shop.main_screen.business_logic.signIn
import com.example.films_shop.main_screen.business_logic.signUp
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.main_screen.screens.custom_font
import com.example.films_shop.main_screen.screens.test_font
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.BackGroundColorButton
import com.example.films_shop.ui.theme.BackGroundColorButtonLightGray
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    onNavigateToMainScreen: (MainScreenDataObject) -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val backColor = if (isDark) BackGroundColor else Color.White
    val textColor = if (isDark) Color.White else BackGroundColor
    val backColorTextField = if (isDark) BackGroundColorButton else BackGroundColorButtonLightGray
    val auth = remember {
        Firebase.auth
    }
    val emailState = remember { mutableStateOf("rustamquee@gmail.com") }
    val passwordState = remember { mutableStateOf("741963rR") }
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
            text = "Добро пожаловать",
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = test_font,
            color = textColor,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(15.dp))
        RoundedCornerTextField(
            text = emailState.value,
            label = "Почта",
            backColorTextField = backColorTextField
        )
        {
            emailState.value = it
        }
        Spacer(Modifier.height(10.dp))
        RoundedCornerTextField(
            text = passwordState.value,
            label = "Пароль",
            backColorTextField = backColorTextField
        )
        {
            passwordState.value = it
        }
        Spacer(Modifier.height(10.dp))
        if (errorState.value.isNotEmpty()) {
            Text(
                text = errorState.value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = custom_font,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(30.dp))
        LoginButton("Войти", backColor, textColor) {
            signIn(
                auth,
                emailState.value,
                passwordState.value,
                onSignInSuccess = { navData ->
                    onNavigateToMainScreen(navData)
                },
                onSignInFailure = { error ->
                    errorState.value = error
                }
            )
        }
        Spacer(Modifier.height(10.dp))
        LoginButton("Регистрация", backColor, textColor) {
            signUp(
                auth,
                emailState.value,
                passwordState.value,
                onSignUpSuccess = { navData ->
                    onNavigateToMainScreen(navData)
                },
                onSignUpFailure = { error ->
                    errorState.value = error
                }
            )
        }
    }
}