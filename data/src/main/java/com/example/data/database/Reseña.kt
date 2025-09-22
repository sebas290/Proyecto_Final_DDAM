package com.example.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "reseñas",
    foreignKeys = [
        ForeignKey(entity = Usuarios::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Juegos::class, parentColumns = ["id"], childColumns = ["juegoId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("usuarioId"), Index("juegoId")]
)
data class Reseña(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val videojuegoId: Int,
    val estrellas: Int,
    val comentario: String? = null,
    val fecha: LocalDateTime
)
