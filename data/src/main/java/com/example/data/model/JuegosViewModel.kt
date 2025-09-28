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

    private var backupReseñas: MutableMap<Int, List<Reseña>> = mutableMapOf()

    init {
        viewModelScope.launch {
            _juegos.value = repository.getJuegosConUsuarios()
        }
    }

    fun addJuego(juego: Juegos) {
        viewModelScope.launch {
            try {
                repository.insert(juego) // 🔥 Inserta en Room + Firebase
                restaurarReseñasSiExisten(juego.id)
                _juegos.value = repository.getJuegosConUsuarios()
            } catch (e: Exception) {
                Log.e("JuegosViewModel", "Error al agregar juego: ${e.message}")
            }
        }
    }

    fun updateJuego(juego: Juegos) {
        viewModelScope.launch {
            try {
                // Crear un juego actualizado con la fecha actual
                val juegoActualizado = juego.copy(fecha = java.time.LocalDateTime.now())

                repository.update(juegoActualizado) // 🔥 Actualiza en Room + Firebase
                _juegos.value = repository.getJuegosConUsuarios()
            } catch (e: Exception) {
                Log.e("JuegosViewModel", "Error al actualizar juego: ${e.message}")
            }
        }
    }

    fun deleteJuego(juego: Juegos) {
        viewModelScope.launch {
            try {
                // 1️⃣ Backup opcional
                val reseñasDelJuego = reviewsRepository.getReseñaPorJuego(juego.id)
                if (reseñasDelJuego.isNotEmpty()) {
                    backupReseñas[juego.id] = reseñasDelJuego
                    Log.d("JuegosViewModel", "Backup creado con ${reseñasDelJuego.size} reseñas")
                }

                // 2️⃣ Borrar las reseñas asociadas
                reseñasDelJuego.forEach { reseña ->
                    reviewsRepository.delete(reseña)
                }

                // 3️⃣ Borrar el juego
                repository.delete(juego)

                // 4️⃣ Actualizar la lista
                _juegos.value = repository.getJuegosConUsuarios()

            } catch (e: Exception) {
                Log.e("JuegosViewModel", "Error al eliminar juego y sus reseñas: ${e.message}")
            }
        }
    }

    fun getJuegoById(id: Int): JuegoConUsuario? {
        return _juegos.value.find { it.juego.id == id }
    }

    private suspend fun restaurarReseñasSiExisten(juegoId: Int) {
        backupReseñas[juegoId]?.let { reseñasBackup ->
            reseñasBackup.forEach { reseña ->
                try {
                    reviewsRepository.insert(reseña)
                } catch (e: Exception) {
                    Log.e("JuegosViewModel", "Error al restaurar reseña: ${e.message}")
                }
            }
            reviewsRepository.recalcularPromedioYActualizarJuego(juegoId)
            backupReseñas.remove(juegoId)
        }
    }

    fun clearOldBackups() {
        backupReseñas.clear()
    }

    fun hasBackupForGame(juegoId: Int): Boolean {
        return backupReseñas.containsKey(juegoId)
    }
}