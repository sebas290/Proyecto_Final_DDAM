package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.ClasesRelacionales.JuegoConUsuario
import com.example.data.database.Juegos
import kotlinx.coroutines.flow.Flow

@Dao
interface JuegoDao {
    @Insert
    suspend fun insert(juego: Juegos)

    @Insert
    suspend fun insertarJuego(juego: Juegos)

    @Update
    suspend fun update(juego: Juegos)

    @Delete
    suspend fun delete(juego: Juegos)

    @Query("SELECT * FROM juegos")
    suspend fun getAll(): List<Juegos>

    @Query("SELECT * FROM juegos WHERE id = :id")
    suspend fun getById(id: Int): Juegos?


    @Transaction
    @Query("SELECT * FROM juegos")
    suspend fun getJuegosConUsuarios(): List<JuegoConUsuario>

    @Query("SELECT * FROM juegos")
    fun obtenerJuegos(): Flow<List<Juegos>>

}
