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

        @Transaction
        @Query("SELECT * FROM reseñas WHERE videojuegoId = :juegoId ORDER BY fecha DESC")
        suspend fun getReseñasConUsuario(juegoId: Int): List<ReseñaConUsuarioYJuego>

        @Transaction
        @Query("SELECT * FROM reseñas")
        suspend fun getReseñasConUsuarioYJuego(): List<ReseñaConUsuarioYJuego>
    }
