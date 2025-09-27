package com.example.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ClasesRelacionales.ReseñaConUsuarioYJuego
import com.example.data.database.Reseña
import com.example.data.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ReviewsRepository) : ViewModel() {

    private val _reseñas = MutableStateFlow<List<Reseña>>(emptyList())
    val reseñas: StateFlow<List<Reseña>> get() = _reseñas

    private val _reseñasConUsuario = MutableStateFlow<List<ReseñaConUsuarioYJuego>>(emptyList())
    val reseñasConUsuario: StateFlow<List<ReseñaConUsuarioYJuego>> get() = _reseñasConUsuario

    fun addReseña(resena: Reseña) {
        viewModelScope.launch {
            try {
                repository.insertAndRecalcularPromedio(resena)
                // actualizar estado solo si la inserción tuvo éxito
                _reseñas.value = repository.getReseñaPorJuego(resena.videojuegoId)
                _reseñasConUsuario.value = repository.getReseñaConUsuario(resena.videojuegoId)
            } catch (e: Exception) {
                // capturamos y logueamos. Evita que la app muera por una excepción Room.
                android.util.Log.e("ReviewViewModel", "Error al insertar reseña", e)
                // Opcional: exponer un StateFlow para la UI muestre un Toast
            }
        }
    }

    fun updateReseña(resena: Reseña) {
        viewModelScope.launch {
            repository.update(resena)
            repository.recalcularPromedioYActualizarJuego(resena.videojuegoId)
            _reseñas.value = repository.getReseñaPorJuego(resena.videojuegoId)
            _reseñasConUsuario.value = repository.getReseñaConUsuario(resena.videojuegoId)
        }
    }

    fun deleteReseña(resena: Reseña) {
        viewModelScope.launch {
            repository.delete(resena)
            repository.recalcularPromedioYActualizarJuego(resena.videojuegoId)
            _reseñas.value = repository.getReseñaPorJuego(resena.videojuegoId)
            _reseñasConUsuario.value = repository.getReseñaConUsuario(resena.videojuegoId)
        }
    }

    fun getReviewPorJuego(juegoId: Int) {
        viewModelScope.launch {
            _reseñas.value = repository.getReseñaPorJuego(juegoId)
            _reseñasConUsuario.value = repository.getReseñaConUsuario(juegoId)
        }
    }

    fun getTodasLasReseñas() {
        viewModelScope.launch {
            _reseñasConUsuario.value = repository.getReseñaConUsuarioYJuego()
        }
    }
}
