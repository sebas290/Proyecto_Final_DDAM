package com.example.data.repository

import com.example.data.ClasesRelacionales.ReseñaConUsuarioYJuego
import com.example.data.dao.JuegoDao
import com.example.data.dao.ReseñaDao
import com.example.data.database.Reseña
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import android.util.Log

class ReviewsRepository(
    private val reseñaDao: ReseñaDao,
    private val juegoDao: JuegoDao,
    private val reseñasCollection: CollectionReference
) {

    // Función para convertir Reseña a Map compatible con Firebase
    private fun reseñaToFirebaseMap(reseña: Reseña): Map<String, Any?> {
        return mapOf(
            "roomId" to reseña.id, // 🔥 NUEVO: Guardar el ID de Room
            "usuarioId" to reseña.usuarioId,
            "videojuegoId" to reseña.videojuegoId,
            "estrellas" to reseña.estrellas,
            "comentario" to reseña.comentario,
            "fecha" to reseña.fecha.toString()
        )
    }

    suspend fun insert(reseña: Reseña) {
        try {
            // 1. Insertar en Room y obtener el ID autogenerado
            val roomId = reseñaDao.insert(reseña)
            val reseñaConId = reseña.copy(id = roomId.toInt())

            // 2. Insertar en Firebase usando add() para generar ID único
            val documentRef = reseñasCollection.add(reseñaToFirebaseMap(reseñaConId)).await()

            Log.d("ReviewsRepository", "Reseña insertada: Room ID=$roomId, Firebase ID=${documentRef.id}")
        } catch (e: Exception) {
            Log.e("ReviewsRepository", "Error al insertar reseña", e)
            throw e
        }
    }

    suspend fun update(reseña: Reseña) {
        try {
            // Actualizar en Room
            reseñaDao.update(reseña)

            // 🔥 PROBLEMA: Necesitas mapear Room ID con Firebase ID
            // Para simplificar, vamos a buscar y actualizar por roomId
            val querySnapshot = reseñasCollection
                .whereEqualTo("roomId", reseña.id)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.reference.set(reseñaToFirebaseMap(reseña)).await()
                Log.d("ReviewsRepository", "Reseña actualizada correctamente")
            } else {
                Log.w("ReviewsRepository", "No se encontró la reseña en Firebase con roomId: ${reseña.id}")
            }

        } catch (e: Exception) {
            Log.e("ReviewsRepository", "Error al actualizar reseña", e)
            throw e
        }
    }

    suspend fun delete(reseña: Reseña) {
        try {
            // Borrar de Room
            reseñaDao.delete(reseña)

            // Borrar de Firebase buscando por roomId
            val querySnapshot = reseñasCollection
                .whereEqualTo("roomId", reseña.id)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                document.reference.delete().await()
            }

            Log.d("ReviewsRepository", "Reseña eliminada correctamente de Room y Firebase")
        } catch (e: Exception) {
            Log.e("ReviewsRepository", "Error al eliminar reseña", e)
            throw e
        }
    }

    // 🔥 NUEVA FUNCIÓN: Borrar todas las reseñas de un juego específico
    suspend fun deleteReseñasPorJuego(juegoId: Int) {
        try {
            // 1. Obtener todas las reseñas del juego desde Room
            val reseñas = reseñaDao.getReseñasPorJuego(juegoId)

            // 2. Borrar cada reseña individualmente (esto maneja Room + Firebase)
            for (reseña in reseñas) {
                delete(reseña) // Usa la función delete que ya maneja ambos
            }

            Log.d("ReviewsRepository", "Eliminadas ${reseñas.size} reseñas del juego $juegoId")
        } catch (e: Exception) {
            Log.e("ReviewsRepository", "Error al eliminar reseñas del juego $juegoId", e)
            throw e
        }
    }

    suspend fun getReseñaPorJuego(juegoId: Int): List<Reseña> = reseñaDao.getReseñasPorJuego(juegoId)

    suspend fun getReseñaPorUsuario(usuarioId: Int): List<Reseña> = reseñaDao.getReseñasPorUsuario(usuarioId)

    suspend fun getReseñaConUsuario(juegoId: Int): List<ReseñaConUsuarioYJuego> = reseñaDao.getReseñasConUsuario(juegoId)

    suspend fun getReseñaConUsuarioYJuego(): List<ReseñaConUsuarioYJuego> = reseñaDao.getReseñasConUsuarioYJuego()

    // Inserta la reseña y recalcula el promedio (actualiza la calificación del juego)
    suspend fun insertAndRecalcularPromedio(reseña: Reseña) {
        insert(reseña) // Ya incluye Room + Firebase
        recalcularPromedioYActualizarJuego(reseña.videojuegoId)
    }

    suspend fun recalcularPromedioYActualizarJuego(juegoId: Int) {
        val avg = reseñaDao.getAverageEstrellas(juegoId) ?: 0.0
        val juego = juegoDao.getById(juegoId)
        if (juego != null) {
            val actualizado = juego.copy(calificacion = avg)
            juegoDao.update(actualizado)
        }
    }
}