package com.example.data.firebase

import com.google.firebase.firestore.FirebaseFirestore

class firebaseJuegos (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
){
    private val collection = firestore.collection("juegos")
}