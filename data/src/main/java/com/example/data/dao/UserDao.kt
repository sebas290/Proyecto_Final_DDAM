package com.example.data.dao

import androidx.room.*
import com.example.data.database.Usuarios

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(usuario: Usuarios): Long

    @Update
    suspend fun update(usuario: Usuarios)

    @Delete
    suspend fun delete(usuario: Usuarios)

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getById(id: Int): Usuarios?

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getByCorreo(correo: String): Usuarios?

    @Query("SELECT * FROM usuarios WHERE alias = :alias LIMIT 1")
    suspend fun getByAlias(alias: String): Usuarios?

}
