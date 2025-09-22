package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.data.database.Juegos

@Dao
interface JuegoDao {
    @Insert
    suspend fun insert(juego: Juegos)

    @Update
    suspend fun update(juego: Juegos)

    @Delete
    suspend fun delete(juego: Juegos)

    @Query("SELECT * FROM juegos")
    suspend fun getAll(): List<Juegos>

    @Query("SELECT * FROM juegos WHERE id = :id")
    suspend fun getById(id: Int): Juegos?

    @Query("SELECT * FROM juegos WHERE colaboradorId = :usuarioId")
    suspend fun getJuegosPorColaborador(usuarioId: Int): List<Juegos>
}
