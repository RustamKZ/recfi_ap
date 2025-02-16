package com.example.films_shop.main_screen.login

import android.util.Log
import androidx.compose.runtime.Composable
import com.example.films_shop.main_screen.login.data_nav.MainScreenDataObject
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignUpSuccess: (MainScreenDataObject) -> Unit,
    onSignUpFailure: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank())
    {
        onSignUpFailure("Email or password cannot be empty!")
        return
    }
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignUpSuccess(
                    MainScreenDataObject(
                        task.result.user?.uid!!,
                        task.result.user?.email!!
                    )
                )
            }

        }
        .addOnFailureListener { task ->
            onSignUpFailure(task.message ?: "Sign Up Error!")
        }

}

fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignInSuccess: (MainScreenDataObject) -> Unit,
    onSignInFailure: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank())
    {
        onSignInFailure("Email or password cannot be empty!")
        return
    }
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignInSuccess(
                    MainScreenDataObject(
                        task.result.user?.uid!!,
                        task.result.user?.email!!
                    )
                )
            }
        }
        .addOnFailureListener { task ->
            onSignInFailure(task.message ?: "Sign In Error!")
        }

}