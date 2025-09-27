package Filtro

import com.example.data.database.Juegos
import com.example.data.model.JuegosViewModel
import com.example.data.model.ReviewViewModel
import kotlinx.coroutines.*
import kotlin.math.round
import android.util.Log


object CalificacionHelper {
    suspend fun calcularPromedioCalificacion(
        juegoId: Int,
        reviewViewModel: ReviewViewModel
    ): Double {
        return withContext(Dispatchers.IO) {
            try {
                // Obtener todas las reseñas del juego
                reviewViewModel.getReviewPorJuego(juegoId)
                delay(100) // Dar tiempo para que se actualice el StateFlow

                val reseñas = reviewViewModel.reseñasConUsuario.value
                    .filter { it.reseña.videojuegoId == juegoId }

                if (reseñas.isEmpty()) {
                    return@withContext 0.0
                }

                val suma = reseñas.sumOf { it.reseña.estrellas }
                val promedio = suma.toDouble() / reseñas.size

                // Redondear a 1 decimal
                round(promedio * 10) / 10.0
            } catch (e: Exception) {
                Log.e("CalificacionHelper", "Error calculando promedio: ${e.message}")
                0.0
            }
        }
    }

    /**
     * Actualiza la calificación de un juego específico
     */
    suspend fun actualizarCalificacionJuego(
        juegoId: Int,
        juegosViewModel: JuegosViewModel,
        reviewViewModel: ReviewViewModel
    ) {
        withContext(Dispatchers.IO) {
            try {
                val nuevaCalificacion = calcularPromedioCalificacion(juegoId, reviewViewModel)
                val juegoConUsuario = juegosViewModel.getJuegoById(juegoId)

                juegoConUsuario?.let { juegoConUsuarioData ->
                    val juegoActual = juegoConUsuarioData.juego
                    val juegoActualizado = juegoActual.copy(calificacion = nuevaCalificacion)
                    juegosViewModel.updateJuego(juegoActualizado)
                }
            } catch (e: Exception) {
                Log.e("CalificacionHelper", "Error actualizando calificación: ${e.message}")
            }
        }
    }

    /**
     * Obtiene estadísticas detalladas de las reseñas de un juego
     */
    suspend fun obtenerEstadisticasJuego(
        juegoId: Int,
        reviewViewModel: ReviewViewModel
    ): EstadisticasJuego {
        return withContext(Dispatchers.IO) {
            try {
                reviewViewModel.getReviewPorJuego(juegoId)
                delay(100)

                val reseñas = reviewViewModel.reseñasConUsuario.value
                    .filter { it.reseña.videojuegoId == juegoId }
                    .map { it.reseña }

                if (reseñas.isEmpty()) {
                    return@withContext EstadisticasJuego()
                }

                val totalReseñas = reseñas.size
                val sumaEstrellas = reseñas.sumOf { it.estrellas }
                val promedio = sumaEstrellas.toDouble() / totalReseñas

                // Distribución por estrellas (1-10)
                val distribucion = (1..10).associateWith { estrella ->
                    reseñas.count { it.estrellas == estrella }
                }

                val calificacionMaxima = reseñas.maxOfOrNull { it.estrellas } ?: 0
                val calificacionMinima = reseñas.minOfOrNull { it.estrellas } ?: 0

                EstadisticasJuego(
                    totalReseñas = totalReseñas,
                    calificacionPromedio = round(promedio * 10) / 10.0,
                    calificacionMaxima = calificacionMaxima,
                    calificacionMinima = calificacionMinima,
                    distribucionEstrellas = distribucion,
                    reseñasConComentario = reseñas.count { !it.comentario.isNullOrBlank() }
                )
            } catch (e: Exception) {
                Log.e("CalificacionHelper", "Error obteniendo estadísticas: ${e.message}")
                EstadisticasJuego()
            }
        }
    }

    /**
     * Filtra juegos por rango de calificación y los ordena por calificación descendente
     */
    fun filtrarJuegosPorCalificacion(
        juegos: List<com.example.data.ClasesRelacionales.JuegoConUsuario>,
        calificacionMinima: Double = 0.0,
        calificacionMaxima: Double = 10.0
    ): List<com.example.data.ClasesRelacionales.JuegoConUsuario> {
        return juegos.filter { juegoConUsuario ->
            val calificacion = juegoConUsuario.juego.calificacion
            calificacion >= calificacionMinima && calificacion <= calificacionMaxima
        }.sortedByDescending { it.juego.calificacion }
    }

    /**
     * Obtiene los juegos mejor calificados
     */
    fun obtenerJuegosMejorCalificados(
        juegos: List<com.example.data.ClasesRelacionales.JuegoConUsuario>,
        limite: Int = 10
    ): List<com.example.data.ClasesRelacionales.JuegoConUsuario> {
        return juegos
            .filter { it.juego.calificacion > 0.0 }
            .sortedByDescending { it.juego.calificacion }
            .take(limite)
    }

    /**
     * Categoriza la calificación en texto descriptivo
     */
    fun obtenerCategoriaCalificacion(calificacion: Double): String {
        return when {
            calificacion == 0.0 -> "Sin calificar"
            calificacion <= 2.0 -> "Muy malo"
            calificacion <= 4.0 -> "Malo"
            calificacion <= 6.0 -> "Regular"
            calificacion <= 8.0 -> "Bueno"
            calificacion <= 9.0 -> "Excelente"
            else -> "Increíble"
        }
    }

    /**
     * Actualiza todas las calificaciones de los juegos existentes
     */
    suspend fun actualizarTodasLasCalificaciones(
        juegosViewModel: JuegosViewModel,
        reviewViewModel: ReviewViewModel
    ) {
        withContext(Dispatchers.IO) {
            try {
                val juegos = juegosViewModel.juegos.value

                juegos.forEach { juegoConUsuario ->
                    val juegoId = juegoConUsuario.juego.id
                    actualizarCalificacionJuego(juegoId, juegosViewModel, reviewViewModel)
                    delay(50) // Pequeña pausa entre actualizaciones
                }
            } catch (e: Exception) {
                Log.e("CalificacionHelper", "Error actualizando todas las calificaciones: ${e.message}")
            }
        }
    }
}

/**
 * Data class para las estadísticas de un juego
 */
data class EstadisticasJuego(
    val totalReseñas: Int = 0,
    val calificacionPromedio: Double = 0.0,
    val calificacionMaxima: Int = 0,
    val calificacionMinima: Int = 0,
    val distribucionEstrellas: Map<Int, Int> = emptyMap(),
    val reseñasConComentario: Int = 0
) {
    val tieneReseñas: Boolean get() = totalReseñas > 0

    val categoriaCalificacion: String get() = CalificacionHelper.obtenerCategoriaCalificacion(calificacionPromedio)

    val porcentajeConComentario: Double get() =
        if (totalReseñas > 0) (reseñasConComentario * 100.0) / totalReseñas else 0.0
}