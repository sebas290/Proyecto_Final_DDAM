package com.example.data.repository

import com.example.data.ClasesRelacionales.JuegoConUsuario
import com.example.data.database.Juegos
import com.example.data.dao.JuegoDao
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class JuegoRepository(
    private val juegoDao: JuegoDao,
    private val juegosCollection: CollectionReference
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
        // Insertar en Room y obtener id autogenerado
        val roomId = juegoDao.insert(juego).toInt()

        // Insertar en Firebase usando el mapa convertido
        val juegoConId = juego.copy(id = roomId)
        juegosCollection.document(roomId.toString())
            .set(juegoToFirebaseMap(juegoConId))
            .await()
    }

    suspend fun update(juego: Juegos) {
        //Actualizar en Room
        juegoDao.update(juego)

        //Actualizar en Firebase usando el mapa convertido
        juegosCollection.document(juego.id.toString())
            .set(juegoToFirebaseMap(juego))
            .await()
    }

    suspend fun delete(juego: Juegos) {
        // Borrar en Room
        juegoDao.delete(juego)

        // Borrar en Firebase
        juegosCollection.document(juego.id.toString()).delete().await()
    }

    suspend fun getAllJuegos(): List<Juegos> = juegoDao.getAll()

    suspend fun getById(id: Int): Juegos? = juegoDao.getById(id)

    suspend fun getJuegosConUsuarios(): List<JuegoConUsuario> = juegoDao.getJuegosConUsuarios()

    fun obtenerJuegosFlow(): Flow<List<Juegos>> = juegoDao.obtenerJuegos()
}