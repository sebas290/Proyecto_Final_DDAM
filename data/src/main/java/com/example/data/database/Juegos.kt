package com.example.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "juegos",
    foreignKeys = [
        ForeignKey(
            entity = Usuarios::class,
            parentColumns = ["id"],
            childColumns = ["colaboradorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("colaboradorId")]
)
data class Juegos(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val genero: String,
    val calificacion: Double = 0.0,
    val descripcion: String,
    val fecha: LocalDateTime,
    val colaboradorId: Int,
    val archivoUri: String? = null

)
