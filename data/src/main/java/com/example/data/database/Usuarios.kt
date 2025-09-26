package com.example.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios", indices = [Index(value = ["correo"], unique = true), Index(value = ["alias"], unique = true)])

data class Usuarios(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val alias: String,
    val correo: String,
    val password: String
)
