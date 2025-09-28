package com.example.data.database

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import kotlinx.coroutines.delay

class SyncDataWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_INPUT_DATA = "input_data"
        const val KEY_OUTPUT_DATA = "output_data"
        const val KEY_ERROR_MESSAGE = "error_message"
        const val WORK_NAME = "sync_data_work"

        private const val TAG = "SyncDataWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Iniciando SyncDataWorker")

            // Obtener datos de entrada
            val inputData = inputData.getString(KEY_INPUT_DATA) ?: "Sin datos"
            Log.d(TAG, "Datos de entrada: $inputData")

            // Procesar según el tipo de sincronización
            when (inputData) {
                "sync_juegos_firebase" -> syncJuegos()
                "sync_reseñas_firebase" -> syncReseñas()
                "full_sync_all_data" -> fullSync()
                else -> defaultSync(inputData)
            }

        } catch (exception: Exception) {
            Log.e(TAG, "Error durante la ejecución del trabajo", exception)

            // Crear datos de error
            val errorData = Data.Builder()
                .putString(KEY_ERROR_MESSAGE, exception.message ?: "Error desconocido")
                .putLong("error_time", System.currentTimeMillis())
                .build()

            // Decidir si reintentar o fallar definitivamente
            return if (runAttemptCount < 3) {
                Log.d(TAG, "Reintentando... Intento ${runAttemptCount + 1}")
                Result.retry()
            } else {
                Log.e(TAG, "Máximo número de reintentos alcanzado")
                Result.failure(errorData)
            }
        }
    }

    private suspend fun syncJuegos(): Result {
        Log.d(TAG, "Sincronizando juegos...")

        // Verificar si el trabajo fue cancelado
        if (isStopped) {
            return createCancelledResult()
        }

        // Paso 1: Conectar con Firebase
        setProgress(
            Data.Builder()
                .putInt("progress", 20)
                .putString("current_step", "Conectando con Firebase...")
                .build()
        )
        delay(1000)

        if (isStopped) return createCancelledResult()

        // Paso 2: Obtener datos
        setProgress(
            Data.Builder()
                .putInt("progress", 50)
                .putString("current_step", "Obteniendo juegos...")
                .build()
        )
        delay(2000)

        if (isStopped) return createCancelledResult()

        // Paso 3: Procesar datos
        setProgress(
            Data.Builder()
                .putInt("progress", 80)
                .putString("current_step", "Procesando juegos...")
                .build()
        )
        delay(1500)

        if (isStopped) return createCancelledResult()

        // Finalización
        setProgress(
            Data.Builder()
                .putInt("progress", 100)
                .putString("current_step", "Juegos sincronizados")
                .build()
        )

        val outputData = Data.Builder()
            .putString(KEY_OUTPUT_DATA, "Juegos sincronizados exitosamente")
            .putInt("juegos_sincronizados", 25)
            .putLong("completion_time", System.currentTimeMillis())
            .build()

        Log.d(TAG, "Sincronización de juegos completada")
        return Result.success(outputData)
    }

    private suspend fun syncReseñas(): Result {
        Log.d(TAG, "Sincronizando reseñas...")

        if (isStopped) return createCancelledResult()

        setProgress(
            Data.Builder()
                .putInt("progress", 30)
                .putString("current_step", "Sincronizando reseñas...")
                .build()
        )
        delay(2000)

        if (isStopped) return createCancelledResult()

        setProgress(
            Data.Builder()
                .putInt("progress", 70)
                .putString("current_step", "Procesando reseñas...")
                .build()
        )
        delay(1500)

        if (isStopped) return createCancelledResult()

        setProgress(
            Data.Builder()
                .putInt("progress", 100)
                .putString("current_step", "Reseñas sincronizadas")
                .build()
        )

        val outputData = Data.Builder()
            .putString(KEY_OUTPUT_DATA, "Reseñas sincronizadas exitosamente")
            .putInt("reseñas_sincronizadas", 45)
            .putLong("completion_time", System.currentTimeMillis())
            .build()

        return Result.success(outputData)
    }

    private suspend fun fullSync(): Result {
        Log.d(TAG, "Iniciando sincronización completa...")

        val steps = listOf(
            "Preparando sincronización..." to 10,
            "Sincronizando juegos..." to 30,
            "Sincronizando reseñas..." to 60,
            "Sincronizando usuarios..." to 80,
            "Finalizando sincronización..." to 100
        )

        for ((stepMessage, progress) in steps) {
            if (isStopped) return createCancelledResult()

            setProgress(
                Data.Builder()
                    .putInt("progress", progress)
                    .putString("current_step", stepMessage)
                    .build()
            )
            delay(1500) // Simular trabajo
        }

        val outputData = Data.Builder()
            .putString(KEY_OUTPUT_DATA, "Sincronización completa exitosa")
            .putInt("juegos_sincronizados", 25)
            .putInt("reseñas_sincronizadas", 45)
            .putInt("usuarios_actualizados", 12)
            .putLong("completion_time", System.currentTimeMillis())
            .build()

        Log.d(TAG, "Sincronización completa terminada")
        return Result.success(outputData)
    }

    private suspend fun defaultSync(inputData: String): Result {
        Log.d(TAG, "Ejecutando sincronización por defecto con: $inputData")

        // Simular trabajo con progreso
        for (i in 1..5) {
            if (isStopped) return createCancelledResult()

            val progress = i * 20
            setProgress(
                Data.Builder()
                    .putInt("progress", progress)
                    .putString("current_step", "Procesando paso $i de 5")
                    .build()
            )
            delay(2000)
        }

        val outputData = Data.Builder()
            .putString(KEY_OUTPUT_DATA, "Sincronización completada exitosamente")
            .putLong("completion_time", System.currentTimeMillis())
            .putString("processed_data", inputData)
            .putInt("items_processed", 100)
            .build()

        Log.d(TAG, "Trabajo completado exitosamente")
        return Result.success(outputData)
    }

    private fun createCancelledResult(): Result {
        Log.d(TAG, "Trabajo cancelado")
        return Result.failure(
            Data.Builder()
                .putString(KEY_ERROR_MESSAGE, "Trabajo cancelado por el usuario")
                .putLong("cancellation_time", System.currentTimeMillis())
                .build()
        )
    }
}