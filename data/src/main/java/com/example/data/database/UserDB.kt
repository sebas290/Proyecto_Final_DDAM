package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.dao.JuegoDao
import com.example.data.dao.ReseñaDao
import com.example.data.dao.UsuarioDao


@Database(entities = [Usuarios::class, Juegos::class,Reseña::class ], version = 4, exportSchema = false)
@TypeConverters(Converts::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun UserDao(): UsuarioDao
    abstract fun GameDao(): JuegoDao
    abstract fun ReviewDao(): ReseñaDao
}
