package com.example.data.dao

import androidx.room.*
import com.example.data.database.Juego

@Dao
interface JuegoDao {
    @Insert
    suspend fun insert(juego: Juego)

    @Update
    suspend fun update(juego: Juego)

    @Delete
    suspend fun delete(juego: Juego)

    @Query("SELECT * FROM juegos")
    suspend fun getAll(): List<Juego>

    @Query("SELECT * FROM juegos WHERE id = :id")
    suspend fun getById(id: Int): Juego
}
