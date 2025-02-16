package com.example.films_shop.main_screen.data.admin_logic

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

fun isAdmin(onAdmin: (Boolean) -> Unit) {
    val uid = Firebase.auth.currentUser!!.uid
    val db = Firebase.firestore.collection("admin")
        .document(uid).get().addOnSuccessListener {
            onAdmin(it.get("isAdmin") as Boolean)
        }
}