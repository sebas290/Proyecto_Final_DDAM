package com.example.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ClasesRelacionales.ReseñaConUsuarioYJuego
import com.example.data.database.Reseña
import com.example.data.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class ReviewViewModel(private val repository: ReviewsRepository) : ViewModel() {

    private val _reseñas = MutableStateFlow<List<Reseña>>(emptyList())
    val reseñas: StateFlow<List<Reseña>> get() = _reseñas

    private val _reseñasConUsuario = MutableStateFlow<List<ReseñaConUsuarioYJuego>>(emptyList())
    val reseñasConUsuario: StateFlow<List<ReseñaConUsuarioYJuego>> get() = _reseñasConUsuario

    fun addReseña(resena: Reseña) {
        viewModelScope.launch {
            try {
                Log.d("ReviewViewModel", "Iniciando inserción de reseña: $resena")

                // 🔥 USAR insertAndRecalcularPromedio que maneja Room + Firebase + recálculo
                repository.insertAndRecalcularPromedio(resena)

                Log.d("ReviewViewModel", "Reseña insertada exitosamente")

                // Actualizar los estados después de la inserción exitosa
                _reseñas.value = repository.getReseñaPorJuego(resena.videojuegoId)
                _reseñasConUsuario.value = repository.getReseñaConUsuario(resena.videojuegoId)

                Log.d("ReviewViewModel", "Estados actualizados: ${_reseñas.value.size} reseñas totales")

            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error al insertar reseña: ${e.message}", e)
                // Re-lanzar la excepción para que la UI pueda manejarla
                throw e
            }
        }
    }

    fun updateReseña(resena: Reseña) {
        viewModelScope.launch {
            try {
                Log.d("ReviewViewModel", "Actualizando reseña: ${resena.id}")

                repository.update(resena)
                repository.recalcularPromedioYActualizarJuego(resena.videojuegoId)

                // Actualizar estados
                _reseñas.value = repository.getReseñaPorJuego(resena.videojuegoId)
                _reseñasConUsuario.value = repository.getReseñaConUsuario(resena.videojuegoId)

                Log.d("ReviewViewModel", "Reseña actualizada exitosamente")

            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error al actualizar reseña: ${e.message}", e)
                throw e
            }
        }
    }

    fun deleteReseña(resena: Reseña) {
        viewModelScope.launch {
            try {
                Log.d("ReviewViewModel", "Eliminando reseña: ${resena.id}")

                repository.delete(resena)
                repository.recalcularPromedioYActualizarJuego(resena.videojuegoId)

                // Actualizar estados
                _reseñas.value = repository.getReseñaPorJuego(resena.videojuegoId)
                _reseñasConUsuario.value = repository.getReseñaConUsuario(resena.videojuegoId)

                Log.d("ReviewViewModel", "Reseña eliminada exitosamente")

            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error al eliminar reseña: ${e.message}", e)
                throw e
            }
        }
    }

    fun getReviewPorJuego(juegoId: Int) {
        viewModelScope.launch {
            try {
                Log.d("ReviewViewModel", "Obteniendo reseñas para juego: $juegoId")

                _reseñas.value = repository.getReseñaPorJuego(juegoId)
                _reseñasConUsuario.value = repository.getReseñaConUsuario(juegoId)

                Log.d("ReviewViewModel", "Obtenidas ${_reseñas.value.size} reseñas para el juego $juegoId")

            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error al obtener reseñas: ${e.message}", e)
            }
        }
    }

    fun getTodasLasReseñas() {
        viewModelScope.launch {
            try {
                Log.d("ReviewViewModel", "Obteniendo todas las reseñas")

                _reseñasConUsuario.value = repository.getReseñaConUsuarioYJuego()

                Log.d("ReviewViewModel", "Obtenidas ${_reseñasConUsuario.value.size} reseñas totales")

            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error al obtener todas las reseñas: ${e.message}", e)
            }
        }
    }

    // 🔥 NUEVA: Función para refrescar las reseñas después de operaciones externas
    fun refreshReseñas(juegoId: Int) {
        viewModelScope.launch {
            try {
                _reseñas.value = repository.getReseñaPorJuego(juegoId)
                _reseñasConUsuario.value = repository.getReseñaConUsuario(juegoId)
            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error al refrescar reseñas: ${e.message}", e)
            }
        }
    }
}