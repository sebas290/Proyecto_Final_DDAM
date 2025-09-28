package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.database.Reseña
import com.example.data.ClasesRelacionales.ReseñaConUsuarioYJuego

@Dao
interface ReseñaDao {
    // 🔥 CRÍTICO: Debe devolver Long para obtener el ID autogenerado
    @Insert
    suspend fun insert(reseña: Reseña): Long

    @Update
    suspend fun update(reseña: Reseña)

    @Delete
    suspend fun delete(reseña: Reseña)

    @Query("SELECT * FROM reseñas WHERE videojuegoId = :juegoId")
    suspend fun getReseñasPorJuego(juegoId: Int): List<Reseña>

    @Query("SELECT * FROM reseñas WHERE usuarioId = :usuarioId")
    suspend fun getReseñasPorUsuario(usuarioId: Int): List<Reseña>

    @Transaction
    @Query("SELECT * FROM reseñas WHERE videojuegoId = :juegoId ORDER BY fecha DESC")
    suspend fun getReseñasConUsuario(juegoId: Int): List<ReseñaConUsuarioYJuego>

    @Transaction
    @Query("SELECT * FROM reseñas")
    suspend fun getReseñasConUsuarioYJuego(): List<ReseñaConUsuarioYJuego>

    // Consulta para calcular promedio de 'estrellas' por juego
    @Query("SELECT AVG(estrellas) FROM reseñas WHERE videojuegoId = :juegoId")
    suspend fun getAverageEstrellas(juegoId: Int): Double?

    // Función para borrar todas las reseñas de un juego específico
    @Query("DELETE FROM reseñas WHERE videojuegoId = :juegoId")
    suspend fun deleteReseñasPorJuego(juegoId: Int): Int // Devuelve número de filas eliminadas
}