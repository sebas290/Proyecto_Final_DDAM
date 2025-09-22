package com.example.data.repository

import com.example.data.dao.JuegoDao
import com.example.data.database.Juego

class JuegoRepository(private val dao: JuegoDao) {
    suspend fun agregarJuego(juego: Juego) = dao.insert(juego)
    suspend fun actualizarJuego(juego: Juego) = dao.update(juego)
    suspend fun eliminarJuego(juego: Juego) = dao.delete(juego)
    suspend fun obtenerJuegos() = dao.getAll()
}