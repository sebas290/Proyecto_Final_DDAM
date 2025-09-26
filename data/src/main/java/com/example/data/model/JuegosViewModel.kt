package com.example.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ClasesRelacionales.JuegoConUsuario
import com.example.data.database.Juegos
import com.example.data.repository.JuegoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JuegosViewModel(private val repository: JuegoRepository) : ViewModel() {

    private val _juegos = MutableStateFlow<List<JuegoConUsuario>>(emptyList())
    val juegos: StateFlow<List<JuegoConUsuario>> get() = _juegos

    init {
        viewModelScope.launch {
            _juegos.value = repository.getJuegosConUsuarios()
        }
    }

    fun addJuego(juego: Juegos) {
        viewModelScope.launch {
            repository.insert(juego)
            _juegos.value = repository.getJuegosConUsuarios()
        }
    }

    fun updateJuego(juego: Juegos) {
        viewModelScope.launch {
            repository.update(juego)
            _juegos.value = repository.getJuegosConUsuarios()
        }
    }

    fun deleteJuego(juego: Juegos) {
        viewModelScope.launch {
            repository.delete(juego)
            _juegos.value = repository.getJuegosConUsuarios()
        }
    }

    fun getJuegoById(id: Int): JuegoConUsuario? {
        return _juegos.value.find { it.juego.id == id }
    }
}