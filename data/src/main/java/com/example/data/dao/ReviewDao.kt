package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.data.database.Reseña
import java.time.LocalDateTime

data class ReseñaConUsuario(
    val id: Int,
    val alias: String,
    val estrellas: Int,
    val comentario: String?,
    val fecha: LocalDateTime
)

@Dao
interface ReseñaDao {
    @Insert
    suspend fun insert(reseña: Reseña)

    @Update
    suspend fun update(reseña: Reseña)

    @Delete
    suspend fun delete(reseña: Reseña)

    @Query("SELECT * FROM reseñas WHERE videojuegoId = :juegoId")
    suspend fun getReseñasPorJuego(juegoId: Int): List<Reseña>

    @Query("SELECT * FROM reseñas WHERE usuarioId = :usuarioId")
    suspend fun getReseñasPorUsuario(usuarioId: Int): List<Reseña>

    @Query("SELECT r.id, u.alias, r.estrellas, r.comentario, r.fecha FROM reseñas r INNER JOIN usuarios u ON r.usuarioId = u.id WHERE r.videojuegoId = :juegoId ORDER BY r.fecha DESC")
    suspend fun getReseñasConUsuario(juegoId: Int): List<ReseñaConUsuario>
}
