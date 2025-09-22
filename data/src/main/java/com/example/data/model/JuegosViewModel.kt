package com.example.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.Juegos
import com.example.data.repository.JuegoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JuegosViewModel(private val repository: JuegoRepository) : ViewModel() {

    private val _juegos = MutableStateFlow<List<Juegos>>(emptyList())
    val juegos: StateFlow<List<Juegos>> get() = _juegos

    init {
        viewModelScope.launch {
            _juegos.value = repository.getAllJuegos()
        }
    }

    fun addJuego(juego: Juegos) {
        viewModelScope.launch {
            repository.insert(juego)
            _juegos.value = repository.getAllJuegos()
        }
    }

    fun updateJuego(juego: Juegos) {
        viewModelScope.launch {
            repository.update(juego)
            _juegos.value = repository.getAllJuegos()
        }
    }

    fun deleteJuego(juego: Juegos) {
        viewModelScope.launch {
            repository.delete(juego)
            _juegos.value = repository.getAllJuegos()
        }
    }

    fun getJuegoById(id: Int): Juegos? {
        return _juegos.value.find { it.id == id }
    }
}