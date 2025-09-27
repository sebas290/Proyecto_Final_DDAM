package com.example.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.example.data.database.Juegos
import android.util.Log
val collection = FirebaseFirestore.getInstance().collection("juegos")

fun addJuegoToFirestore(
    juego: Juegos,
    onResult: (success: Boolean, message: String?) -> Unit = { _, _ -> }
) {
    try {
        val data = hashMapOf(
            "titulo" to juego.titulo,
            "genero" to juego.genero,
            "calificacion" to juego.calificacion,
            "descripcion" to juego.descripcion,
            "fecha" to juego.fecha.toString(),
            "colaboradorId" to juego.colaboradorId,
            "archivoUri" to juego.archivoUri
        )
        collection.add(data)
            .addOnSuccessListener { documentReference ->
                onResult(true, documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.e("firebaseJuegos", "Error adding juego to firestore", e)
                onResult(false, e.message)
            }
    } catch (e: Exception) {
        Log.e("firebaseJuegos", "Exception preparing juego for firestore", e)
        onResult(false, e.message)
    }
}