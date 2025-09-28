package com.example.data.repository

import com.example.data.ClasesRelacionales.JuegoConUsuario
import com.example.data.database.Juegos
import com.example.data.dao.JuegoDao
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import android.util.Log

class JuegoRepository(
    private val juegoDao: JuegoDao,
    private val juegosCollection: CollectionReference,
    private val reseñasCollection: CollectionReference // Para manejar las reseñas
) {

    // Función para convertir Juegos a Map compatible con Firebase
    private fun juegoToFirebaseMap(juego: Juegos): Map<String, Any?> {
        return mapOf(
            "id" to juego.id,
            "titulo" to juego.titulo,
            "descripcion" to juego.descripcion,
            "genero" to juego.genero,
            "calificacion" to juego.calificacion,
            "colaboradorId" to juego.colaboradorId,
            "archivoUri" to juego.archivoUri?.toString(),
            "fecha" to juego.fecha.toString()
        )
    }

    suspend fun insert(juego: Juegos) {
        try {
            // Insertar en Room y obtener id autogenerado
            val roomId = juegoDao.insert(juego).toInt()

            // Insertar en Firebase usando el mapa convertido
            val juegoConId = juego.copy(id = roomId)
            juegosCollection.document(roomId.toString())
                .set(juegoToFirebaseMap(juegoConId))
                .await()

            Log.d("JuegoRepository", "Juego insertado correctamente con ID: $roomId")
        } catch (e: Exception) {
            Log.e("JuegoRepository", "Error al insertar juego", e)
            throw e
        }
    }

    suspend fun update(juego: Juegos) {
        try {
            //Actualizar en Room
            juegoDao.update(juego)

            //Actualizar en Firebase usando el mapa convertido
            juegosCollection.document(juego.id.toString())
                .set(juegoToFirebaseMap(juego))
                .await()

            Log.d("JuegoRepository", "Juego actualizado correctamente")
        } catch (e: Exception) {
            Log.e("JuegoRepository", "Error al actualizar juego", e)
            throw e
        }
    }

    suspend fun delete(juego: Juegos, reviewsRepository: ReviewsRepository) {
        try {
            // 1️⃣ PRIMERO: Borrar todas las reseñas usando el ReviewsRepository
            reviewsRepository.deleteReseñasPorJuego(juego.id)

            // 2️⃣ SEGUNDO: Borrar el juego de Room
            juegoDao.delete(juego)

            // 3️⃣ TERCERO: Borrar el juego de Firebase
            juegosCollection.document(juego.id.toString()).delete().await()

            Log.d("JuegoRepository", "Juego ${juego.id} eliminado completamente con todas sus reseñas")

        } catch (e: Exception) {
            Log.e("JuegoRepository", "Error al eliminar juego y sus reseñas", e)
            throw e
        }
    }

    suspend fun getAllJuegos(): List<Juegos> = juegoDao.getAll()

    suspend fun getById(id: Int): Juegos? = juegoDao.getById(id)

    suspend fun getJuegosConUsuarios(): List<JuegoConUsuario> = juegoDao.getJuegosConUsuarios()

    fun obtenerJuegosFlow(): Flow<List<Juegos>> = juegoDao.obtenerJuegos()
}