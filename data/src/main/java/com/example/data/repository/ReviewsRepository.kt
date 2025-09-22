package com.example.data.repository

import com.example.data.dao.ReseñaConUsuario
import com.example.data.dao.ReseñaDao
import com.example.data.database.Reseña

class ReviewsRepository(private val reseñaDao: ReseñaDao) {

    suspend fun insert(reseña: Reseña) = reseñaDao.insert(reseña)

    suspend fun update(reseña: Reseña) = reseñaDao.update(reseña)

    suspend fun delete(reseña: Reseña) = reseñaDao.delete(reseña)

    suspend fun getResenasPorJuego(juegoId: Int): List<Reseña> = reseñaDao.getReseñasPorJuego(juegoId)

    suspend fun getResenasPorUsuario(usuarioId: Int): List<Reseña> = reseñaDao.getReseñasPorUsuario(usuarioId)

    suspend fun getResenasConUsuario(juegoId: Int): List<ReseñaConUsuario> = reseñaDao.getReseñasConUsuario(juegoId)
}