package com.example.data.repository

import com.example.data.ClasesRelacionales.JuegoConUsuario
import com.example.data.dao.JuegoDao
import com.example.data.database.Juegos
import kotlinx.coroutines.flow.Flow

class JuegoRepository(private val juegoDao: JuegoDao) {

    suspend fun insert(juego: Juegos) = juegoDao.insert(juego)

    suspend fun update(juego: Juegos) = juegoDao.update(juego)

    suspend fun delete(juego: Juegos) = juegoDao.delete(juego)

    suspend fun getAllJuegos(): List<Juegos> = juegoDao.getAll()

    suspend fun getReviewPorJuego(id: Int): Juegos? = juegoDao.getById(id)

    suspend fun getJuegosConUsuarios(): List<JuegoConUsuario> = juegoDao.getJuegosConUsuarios()


}
