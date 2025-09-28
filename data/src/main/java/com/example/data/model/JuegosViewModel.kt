package com.example.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ClasesRelacionales.JuegoConUsuario
import com.example.data.database.Juegos
import com.example.data.database.Reseña
import com.example.data.repository.JuegoRepository
import com.example.data.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class JuegosViewModel(
    private val repository: JuegoRepository,
    private val reviewsRepository: ReviewsRepository
) : ViewModel() {

    private val _juegos = MutableStateFlow<List<JuegoConUsuario>>(emptyList())
    val juegos: StateFlow<List<JuegoConUsuario>> get() = _juegos

    // Almacenar reseñas eliminadas para poder restaurarlas
    private var reseñasEliminadas: List<Reseña> = emptyList()

    init {
        viewModelScope.launch {
            _juegos.value = repository.getJuegosConUsuarios()
        }
    }

    fun addJuego(juego: Juegos) {
        viewModelScope.launch {
            try {
                repository.insert(juego)
                _juegos.value = repository.getJuegosConUsuarios()
                Log.d("JuegosViewModel", "Juego agregado correctamente")
            } catch (e: Exception) {
                Log.e("JuegosViewModel", "Error al agregar juego: ${e.message}")
            }
        }
    }

    fun updateJuego(juego: Juegos) {
        viewModelScope.launch {
            try {
                val juegoActualizado = juego.copy(fecha = java.time.LocalDateTime.now())
                repository.update(juegoActualizado)
                _juegos.value = repository.getJuegosConUsuarios()
                Log.d("JuegosViewModel", "Juego actualizado correctamente")
            } catch (e: Exception) {
                Log.e("JuegosViewModel", "Error al actualizar juego: ${e.message}")
            }
        }
    }

    fun deleteJuego(juego: Juegos) {
        viewModelScope.launch {
            try {
                // 1️⃣ PRIMERO: Guardar las reseñas antes de eliminar
                reseñasEliminadas = reviewsRepository.getReseñaPorJuego(juego.id)
                Log.d("JuegosViewModel", "Guardadas ${reseñasEliminadas.size} reseñas para posible recuperación")

                // 2️⃣ SEGUNDO: Eliminar juego y reseñas
                repository.delete(juego, reviewsRepository)

                // 3️⃣ TERCERO: Actualizar la lista
                _juegos.value = repository.getJuegosConUsuarios()

                Log.d("JuegosViewModel", "Juego y sus reseñas eliminados correctamente")

            } catch (e: Exception) {
                Log.e("JuegosViewModel", "Error al eliminar juego y sus reseñas: ${e.message}")
            }
        }
    }

    // Función para restaurar juego con sus reseñas
    fun restaurarJuegoConReseñas(juego: Juegos) {
        viewModelScope.launch {
            try {
                // 1️⃣ Restaurar el juego
                repository.insert(juego)

                // 2️⃣ Restaurar las reseñas una por una
                reseñasEliminadas.forEach { reseña ->
                    // Crear reseña con ID 0 para que Room genere nuevo ID
                    val reseñaNueva = reseña.copy(id = 0)
                    reviewsRepository.insert(reseñaNueva)
                }

                // 3️⃣ Recalcular promedio del juego si había reseñas
                if (reseñasEliminadas.isNotEmpty()) {
                    reviewsRepository.recalcularPromedioYActualizarJuego(juego.id)
                }

                // 4️⃣ Actualizar la lista
                _juegos.value = repository.getJuegosConUsuarios()

                // 5️⃣ Limpiar reseñas eliminadas
                val cantidadRestaurada = reseñasEliminadas.size
                reseñasEliminadas = emptyList()

                Log.d("JuegosViewModel", "Juego restaurado con $cantidadRestaurada reseñas")

            } catch (e: Exception) {
                Log.e("JuegosViewModel", "Error al restaurar juego: ${e.message}")
            }
        }
    }

    fun getJuegoById(id: Int): JuegoConUsuario? {
        return _juegos.value.find { it.juego.id == id }
    }

    fun refreshJuegos() {
        viewModelScope.launch {
            try {
                _juegos.value = repository.getJuegosConUsuarios()
            } catch (e: Exception) {
                Log.e("JuegosViewModel", "Error al refrescar juegos: ${e.message}")
            }
        }
    }
}