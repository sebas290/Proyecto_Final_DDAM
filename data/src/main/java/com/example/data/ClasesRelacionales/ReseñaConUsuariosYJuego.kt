package com.example.data.ClasesRelacionales

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.database.Juegos
import com.example.data.database.Reseña
import com.example.data.database.Usuarios

data class ReseñaConUsuarioYJuego(
    @Embedded val reseña: Reseña,
    @Relation(
        parentColumn = "usuarioId",
        entityColumn = "id"
    )
    val usuario: Usuarios,
    @Relation(
        parentColumn = "videojuegoId",
        entityColumn = "id"
    )
    val juego: Juegos
)
