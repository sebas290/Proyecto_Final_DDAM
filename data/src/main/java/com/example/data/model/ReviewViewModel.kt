package com.example.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.dao.ReseñaConUsuario
import com.example.data.database.Reseña
import com.example.data.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ReviewsRepository) : ViewModel() {

    private val _reseñas = MutableStateFlow<List<Reseña>>(emptyList())
    val reseñas: StateFlow<List<Reseña>> get() = _reseñas

    private val _reseñasConUsuario = MutableStateFlow<List<ReseñaConUsuario>>(emptyList())
    val reseñasConUsuario: StateFlow<List<ReseñaConUsuario>> get() = _reseñasConUsuario

    fun addReseña(resena: Reseña) {
        viewModelScope.launch {
            repository.insert(resena)
            _reseñas.value = repository.getResenasPorJuego(resena.videojuegoId)
            _reseñasConUsuario.value = repository.getResenasConUsuario(resena.videojuegoId)
        }
    }

    fun updateReseña(resena: Reseña) {
        viewModelScope.launch {
            repository.update(resena)
            _reseñas.value = repository.getResenasPorJuego(resena.videojuegoId)
            _reseñasConUsuario.value = repository.getResenasConUsuario(resena.videojuegoId)
        }
    }

    fun deleteReseña(resena: Reseña) {
        viewModelScope.launch {
            repository.delete(resena)
            _reseñas.value = repository.getResenasPorJuego(resena.videojuegoId)
            _reseñasConUsuario.value = repository.getResenasConUsuario(resena.videojuegoId)
        }
    }

    fun getResenasPorJuego(juegoId: Int) {
        viewModelScope.launch {
            _reseñas.value = repository.getResenasPorJuego(juegoId)
            _reseñasConUsuario.value = repository.getResenasConUsuario(juegoId)
        }
    }
}