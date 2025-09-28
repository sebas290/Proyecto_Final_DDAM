package com.example.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference


class FirebaseManager {
    companion object {
        private val firestore by lazy { FirebaseFirestore.getInstance() }

        val juegosCollection: CollectionReference by lazy {
            firestore.collection("juegos")
        }

        val reseñasCollection: CollectionReference by lazy {
            firestore.collection("reseñas")
        }
    }
}
