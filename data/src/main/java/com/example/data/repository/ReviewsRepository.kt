package com.example.data.repository

import com.example.data.ClasesRelacionales.ReseñaConUsuarioYJuego
import com.example.data.dao.JuegoDao
import com.example.data.dao.ReseñaDao
import com.example.data.database.Reseña

class ReviewsRepository(
    private val reseñaDao: ReseñaDao,
    private val juegoDao: JuegoDao
) {

    suspend fun insert(reseña: Reseña) = reseñaDao.insert(reseña)

    suspend fun update(reseña: Reseña) = reseñaDao.update(reseña)

    suspend fun delete(reseña: Reseña) = reseñaDao.delete(reseña)

    suspend fun getReseñaPorJuego(juegoId: Int): List<Reseña> = reseñaDao.getReseñasPorJuego(juegoId)

    suspend fun getReseñaPorUsuario(usuarioId: Int): List<Reseña> = reseñaDao.getReseñasPorUsuario(usuarioId)

    suspend fun getReseñaConUsuario(juegoId: Int): List<ReseñaConUsuarioYJuego> = reseñaDao.getReseñasConUsuario(juegoId)

    suspend fun getReseñaConUsuarioYJuego(): List<ReseñaConUsuarioYJuego> = reseñaDao.getReseñasConUsuarioYJuego()

    // Inserta la reseña y recalcula el promedio (actualiza la calificación del juego)
    suspend fun insertAndRecalcularPromedio(reseña: Reseña) {
        reseñaDao.insert(reseña)
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
