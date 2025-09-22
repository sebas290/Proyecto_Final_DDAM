package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "juegos")

data class Juegos(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val genero: String,
    val calificacion: Int,
    val descripcion: String,
    val fecha: String,
    val colaboradorId: String
)
