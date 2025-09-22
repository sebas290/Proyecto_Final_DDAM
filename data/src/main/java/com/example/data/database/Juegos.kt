package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
@Entity(tableName = "juegos")

data class Juegos(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val genero: String,
    val calificacion: Int,
    val descripcion: String,
    val fecha: LocalDateTime,
    val colaboradorId: Int
)
