package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "juegos")
data class Juego(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val genero: String,
    val calificacion: Int
)
