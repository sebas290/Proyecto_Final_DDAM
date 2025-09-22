package com.example.data.repository

import com.example.data.dao.UsuarioDao
import com.example.data.database.Usuarios

class UsuariosRepository(private val usuarioDao: UsuarioDao) {

    suspend fun insert(usuario: Usuarios): Boolean {
        val id = usuarioDao.insert(usuario)
        return id != -1L
    }

    suspend fun update(usuario: Usuarios): Boolean {
        val existente = usuarioDao.getByCorreo(usuario.correo)
        if (existente != null && existente.id != usuario.id) {

            return false
        }
        usuarioDao.update(usuario)
        return true
    }

    suspend fun delete(usuario: Usuarios) = usuarioDao.delete(usuario)

    suspend fun getUsuarioById(id: Int): Usuarios? = usuarioDao.getById(id)

    suspend fun getUsuarioByCorreo(correo: String): Usuarios? = usuarioDao.getByCorreo(correo)

    suspend fun getUsuarioByAlias(alias: String): Usuarios? = usuarioDao.getByAlias(alias)

}
