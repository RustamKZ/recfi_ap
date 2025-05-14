package com.example.films_shop.main_screen.business_logic

import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
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