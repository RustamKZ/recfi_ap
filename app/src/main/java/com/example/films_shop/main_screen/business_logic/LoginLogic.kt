package com.example.films_shop.main_screen.business_logic

import com.example.films_shop.main_screen.Genres.GenreKP
import com.example.films_shop.main_screen.objects.main_screens_objects.MainScreenDataObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignUpSuccess: (MainScreenDataObject) -> Unit,
    onSignUpFailure: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank())
    {
        onSignUpFailure("Вы не указали почту или пароль!")
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
            onSignUpFailure(task.message ?: "Ошибка регистрации")
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
        onSignInFailure("Вы не указали почту или пароль!")
        return
    }
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignInSuccess(
                    MainScreenDataObject(
                        task.result.user?.uid!!,
                        task.result.user?.email!!,
                        true
                    )
                )
            }
        }
        .addOnFailureListener { task ->
            onSignInFailure(task.message ?: "Ошибка авторизации")
        }

}

fun addOrChangeName(
    db: FirebaseFirestore,
    uid: String,
    name: String,
    onResult: (Boolean) -> Unit
) {
    db.collection("users")
        .document(uid)
        .update("name", name)
        .addOnSuccessListener {
            onResult(true)
        }
        .addOnFailureListener {
            db.collection("users")
                .document(uid)
                .set(mapOf("name" to name))
                .addOnSuccessListener {
                    onResult(true)
                }
                .addOnFailureListener {
                    onResult(false)
                }
        }
}

