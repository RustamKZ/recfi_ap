package com.example.films_shop.main_screen.screens.account

import android.util.Log
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
import com.example.films_shop.main_screen.Genres.AuthorsGoogle
import com.example.films_shop.main_screen.Genres.GenreKP
import com.example.films_shop.main_screen.Genres.loadUserAuthors
import com.example.films_shop.main_screen.Genres.loadUserGenres
import com.example.films_shop.main_screen.business_logic.addOrChangeName
import com.example.films_shop.main_screen.business_logic.signIn
import com.example.films_shop.main_screen.business_logic.signUp
import com.example.films_shop.main_screen.login.LoginButton
import com.example.films_shop.main_screen.login.RoundedCornerTextField
import com.example.films_shop.main_screen.objects.cold_start.ColdStartScreenDataObject
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.main_screen.screens.custom_font
import com.example.films_shop.main_screen.screens.test_font
import com.example.films_shop.ui.theme.BackGroundColor
import com.example.films_shop.ui.theme.BackGroundColorButton
import com.example.films_shop.ui.theme.BackGroundColorButtonLightGray
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    onNavigateToMainScreen: (MainScreenDataObject) -> Unit,
    onNavigateToColdStartScreen: (ColdStartScreenDataObject)-> Unit
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
    val db = remember {
        Firebase.firestore
    }
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
                    loadUserGenres(db, navData.uid) { loadedGenres ->
                        if (loadedGenres.isEmpty()) {
                            onNavigateToColdStartScreen(ColdStartScreenDataObject(navData.uid, navData.email))
                        } else {
                            onNavigateToMainScreen(navData)
                        }
                    }
                },
                onSignInFailure = { exception ->
                    val authException = exception as? FirebaseAuthException
                    val errorCode = authException?.errorCode

                    val message = when (errorCode) {
                        "ERROR_USER_NOT_FOUND" -> "Пользователь с таким email не найден"
                        "ERROR_USER_DISABLED" -> "Аккаунт был отключён"
                        "ERROR_USER_TOKEN_EXPIRED" -> "Срок действия сессии истёк, войдите заново"
                        "ERROR_INVALID_USER_TOKEN" -> "Сбой аутентификации, попробуйте снова"
                        "ERROR_INVALID_EMAIL" -> "Неверный формат email"
                        "ERROR_WRONG_PASSWORD" -> "Неверный пароль"
                        "ERROR_TOO_MANY_REQUESTS" -> "Слишком много попыток. Попробуйте позже."
                        else -> "Ошибка входа"
                    }

                    errorState.value = message
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
                    var loadedGenres: List<GenreKP>? = null
                    var loadedAuthors: List<AuthorsGoogle>? = null

                    fun checkAndNavigate() {
                        if (loadedGenres != null && loadedAuthors != null) {
                            Log.d("loadedGenres", loadedGenres.toString())
                            Log.d("loadedAuthors", loadedAuthors.toString())

                            addOrChangeName(db, navData.uid, "Пользователь") { isSuccess ->
                                // Здесь можно добавить что-то при успехе/ошибке
                            }

                            if (loadedGenres!!.isEmpty() && loadedAuthors!!.isEmpty()) {
                                onNavigateToColdStartScreen(
                                    ColdStartScreenDataObject(navData.uid, navData.email)
                                )
                            } else {
                                onNavigateToMainScreen(navData)
                            }
                        }
                    }

                    loadUserGenres(db, navData.uid) { genres ->
                        loadedGenres = genres
                        checkAndNavigate()
                    }

                    loadUserAuthors(db, navData.uid) { authors ->
                        loadedAuthors = authors
                        checkAndNavigate()
                    }
                },
                onSignUpFailure = { exception ->
                    val authException = exception as? FirebaseAuthException
                    val errorCode = authException?.errorCode

                    val message = when (errorCode) {
                        "ERROR_EMAIL_ALREADY_IN_USE" -> "Этот email уже зарегистрирован"
                        "ERROR_INVALID_EMAIL" -> "Неверный формат email"
                        "ERROR_WEAK_PASSWORD" -> "Пароль слишком слабый"
                        "ERROR_OPERATION_NOT_ALLOWED" -> "Операция регистрации запрещена"
                        else -> "Ошибка регистрации"
                    }

                    errorState.value = message
                }

            )
        }
    }
}