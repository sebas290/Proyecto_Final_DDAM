package com.example.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.Juego
import com.example.data.repository.JuegoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JuegoViewModel(private val repository: JuegoRepository) : ViewModel() {

    private val _juegos = MutableStateFlow<List<Juego>>(emptyList())
    val juegos: StateFlow<List<Juego>> get() = _juegos

    fun cargarJuegos() {
        viewModelScope.launch {
            _juegos.value = repository.obtenerJuegos()
        }
    }

    fun agregarJuego(juego: Juego) {
        viewModelScope.launch {
            repository.agregarJuego(juego)
            cargarJuegos()
        }
    }

    fun actualizarJuego(juego: Juego) {
        viewModelScope.launch {
            repository.actualizarJuego(juego)
            cargarJuegos()
        }
    }

    fun eliminarJuego(juego: Juego) {
        viewModelScope.launch {
            repository.eliminarJuego(juego)
            cargarJuegos()
        }
    }
}
