package com.example.data.ClasesRelacionales

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.database.Juegos
import com.example.data.database.Usuarios

data class JuegoConUsuario(
    @Embedded val juego: Juegos,
    @Relation(
        parentColumn = "colaboradorId",
        entityColumn = "id"
    )
    val usuario: Usuarios
)
