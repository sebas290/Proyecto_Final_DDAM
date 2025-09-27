package com.example.data.firebase

import com.example.data.database.Reseña
import com.google.firebase.firestore.FirebaseFirestore

fun addReseñaToFirestore(
    reseña: Reseña,
    callback: (Boolean, String) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    val reseñaMap = mapOf(
        "usuarioId" to reseña.usuarioId,
        "videojuegoId" to reseña.videojuegoId,
        "estrellas" to reseña.estrellas,
        "comentario" to reseña.comentario,
        "fecha" to com.google.firebase.Timestamp.now()
    )

    firestore.collection("reseñas")
        .add(reseñaMap)
        .addOnSuccessListener {
            callback(true, "Reseña guardada exitosamente")
        }
        .addOnFailureListener { e ->
            callback(false, e.message ?: "Error desconocido")
        }
}