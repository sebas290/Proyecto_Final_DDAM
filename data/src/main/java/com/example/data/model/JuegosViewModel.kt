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
    private val reviewsRepository: ReviewsRepository // Necesitarás inyectar este repository
) : ViewModel() {

    private val _juegos = MutableStateFlow<List<JuegoConUsuario>>(emptyList())
    val juegos: StateFlow<List<JuegoConUsuario>> get() = _juegos

    // Backup temporal para reseñas cuando se elimina un juego
    private var backupReseñas: MutableMap<Int, List<Reseña>> = mutableMapOf()

    init {
        viewModelScope.launch {
            _juegos.value = repository.getJuegosConUsuarios()
        }
    }

    fun addJuego(juego: Juegos) {
        viewModelScope.launch {
            repository.insert(juego)

            // Si hay reseñas en backup para este juego, restaurarlas
            backupReseñas[juego.id]?.let { reseñasBackup ->
                Log.d("JuegosViewModel", "Restaurando ${reseñasBackup.size} reseñas para el juego ${juego.id}")

                // Restaurar cada reseña
                reseñasBackup.forEach { reseña ->
                    try {
                        reviewsRepository.insert(reseña)
                    } catch (e: Exception) {
                        Log.e("JuegosViewModel", "Error al restaurar reseña: ${e.message}")
                    }
                }

                // Recalcular el promedio del juego después de restaurar las reseñas
                reviewsRepository.recalcularPromedioYActualizarJuego(juego.id)

                // Limpiar el backup
                backupReseñas.remove(juego.id)

                Log.d("JuegosViewModel", "Reseñas restauradas exitosamente")
            }

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
            try {
                // PASO 1: Hacer backup de las reseñas ANTES de eliminar el juego
                val reseñasDelJuego = reviewsRepository.getReseñaPorJuego(juego.id)

                if (reseñasDelJuego.isNotEmpty()) {
                    backupReseñas[juego.id] = reseñasDelJuego
                    Log.d("JuegosViewModel", "Backup creado: ${reseñasDelJuego.size} reseñas para el juego ${juego.id}")
                }

                // PASO 2: Eliminar el juego (esto eliminará automáticamente las reseñas por CASCADE)
                repository.delete(juego)

                // PASO 3: Actualizar la lista
                _juegos.value = repository.getJuegosConUsuarios()

            } catch (e: Exception) {
                Log.e("JuegosViewModel", "Error al eliminar juego: ${e.message}")
            }
        }
    }

    fun getJuegoById(id: Int): JuegoConUsuario? {
        return _juegos.value.find { it.juego.id == id }
    }

    // Función para limpiar backups antiguos (opcional, para evitar memory leaks)
    fun clearOldBackups() {
        backupReseñas.clear()
        Log.d("JuegosViewModel", "Backups de reseñas limpiados")
    }

    // Función para verificar si hay backup para un juego (útil para debugging)
    fun hasBackupForGame(juegoId: Int): Boolean {
        return backupReseñas.containsKey(juegoId)
    }
}