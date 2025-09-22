package com.example.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.Usuarios
import com.example.data.repository.UsuariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuariosViewModel(private val repository: UsuariosRepository) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<Usuarios>>(emptyList())
    val usuarios: StateFlow<List<Usuarios>> get() = _usuarios

    init {
        viewModelScope.launch {

        }
    }

    suspend fun addUsuario(usuario: Usuarios): Boolean {
        return repository.insert(usuario)
    }

    suspend fun updateUsuario(usuario: Usuarios): Boolean {
        return repository.update(usuario)
    }

    fun getUsuarioById(id: Int): Usuarios? {
        return _usuarios.value.find { it.id == id }
    }

    suspend fun getUsuarioByCorreo(correo: String): Usuarios? {
        return repository.getUsuarioByCorreo(correo)
    }

    suspend fun getUsuarioByAlias(alias: String): Usuarios? {
        return repository.getUsuarioByAlias(alias)
    }
}