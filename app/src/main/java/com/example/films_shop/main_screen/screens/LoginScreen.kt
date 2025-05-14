package com.example.films_shop.main_screen.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.films_shop.R
import com.example.films_shop.main_screen.login.LoginButton
import com.example.films_shop.main_screen.login.RoundedCornerTextField
import com.example.films_shop.main_screen.business_logic.signIn
import com.example.films_shop.main_screen.business_logic.signUp
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.example.films_shop.ui.theme.BlueForBackground
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    onNavigateToMainScreen: (MainScreenDataObject) -> Unit,
) {
    val auth = remember {
        Firebase.auth
    }
    val emailState = remember { mutableStateOf("rustamquee@gmail.com") }
    val passwordState = remember { mutableStateOf("741963rR") }
    val errorState = remember { mutableStateOf("") }
    Image(
        painter = painterResource(id = R.drawable.films_background_collage),
        contentDescription = "backgroundImage",
        Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueForBackground)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 40.dp,
                end = 40.dp
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.img), contentDescription = "LogoScreen")
        Spacer(Modifier.height(15.dp))
        Text(
            text = "Films shop",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = custom_font,
            color = Color.Black
        )
        Spacer(Modifier.height(15.dp))
        RoundedCornerTextField(
            text = emailState.value,
            label = "Email"
        )
        {
            emailState.value = it
        }
        Spacer(Modifier.height(10.dp))
        RoundedCornerTextField(
            text = passwordState.value,
            label = "Password"
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
        LoginButton("Sign in") {
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
        LoginButton("Sign up") {
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