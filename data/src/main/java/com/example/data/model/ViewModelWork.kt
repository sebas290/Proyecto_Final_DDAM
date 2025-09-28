package com.example.data.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class WorkUiState(
    val workStatus: WorkInfo.State = WorkInfo.State.ENQUEUED,
    val isLoading: Boolean = false,
    val message: String = "Listo para sincronizar",
    val progress: Int = 0,
    val currentStep: String? = null,
    val outputData: String? = null
)

class WorkViewModel @Inject constructor(
    private val workManager: WorkManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkUiState())
    val uiState: StateFlow<WorkUiState> = _uiState.asStateFlow()

    private var currentWorkRequestId: String? = null

    // No need to call observeWorkStatus in init since currentWorkRequestId is null

    // Sincronización manual de juegos
    fun syncJuegosData() {
        viewModelScope.launch {
            val workRequest = OneTimeWorkRequestBuilder<JuegosWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            currentWorkRequestId = workRequest.id.toString()
            workManager.enqueue(workRequest)

            updateUiState(
                isLoading = true,
                message = "Sincronizando juegos...",
                workStatus = WorkInfo.State.RUNNING
            )
        }
    }

    // Sincronización manual de reseñas
    fun syncReseñasData() {
        viewModelScope.launch {
            val workRequest = OneTimeWorkRequestBuilder<ReseñasWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            currentWorkRequestId = workRequest.id.toString()
            workManager.enqueue(workRequest)

            updateUiState(
                isLoading = true,
                message = "Sincronizando reseñas...",
                workStatus = WorkInfo.State.RUNNING
            )
        }
    }

    // Sincronización completa
    fun fullSync() {
        viewModelScope.launch {
            // Crear una cadena de trabajos
            val juegosWork = OneTimeWorkRequestBuilder<JuegosWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            val reseñasWork = OneTimeWorkRequestBuilder<ReseñasWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            // Ejecutar en cadena: primero juegos, luego reseñas
            workManager.beginWith(juegosWork)
                .then(reseñasWork)
                .enqueue()

            currentWorkRequestId = juegosWork.id.toString()

            updateUiState(
                isLoading = true,
                message = "Sincronización completa iniciada...",
                workStatus = WorkInfo.State.RUNNING
            )
        }
    }

    // Activar sincronización automática periódica
    fun enableAutoSync() {
        viewModelScope.launch {
            val periodicWorkRequest = PeriodicWorkRequestBuilder<FullSyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                "auto_sync",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
            )

            updateUiState(
                message = "Sincronización automática activada (cada 15 min)"
            )
        }
    }

    // Programar sincronización para un tiempo específico
    fun scheduledSync(delayInMinutes: Int) {
        viewModelScope.launch {
            val scheduledWorkRequest = OneTimeWorkRequestBuilder<FullSyncWorker>()
                .setInitialDelay(delayInMinutes.toLong(), TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            workManager.enqueue(scheduledWorkRequest)

            updateUiState(
                message = "Sincronización programada para $delayInMinutes minutos"
            )
        }
    }

    // Cancelar trabajo actual
    fun cancelSync() {
        currentWorkRequestId?.let { workId ->
            workManager.cancelWorkById(java.util.UUID.fromString(workId))
            updateUiState(
                isLoading = false,
                message = "Sincronización cancelada",
                workStatus = WorkInfo.State.CANCELLED
            )
        }
    }

    // Cancelar todos los trabajos
    fun cancelAllWork() {
        workManager.cancelAllWork()
        updateUiState(
            isLoading = false,
            message = "Todos los trabajos cancelados",
            workStatus = WorkInfo.State.CANCELLED
        )
    }

    private fun observeWorkStatus(workId: String) {
        viewModelScope.launch {
            try {
                val uuid = java.util.UUID.fromString(workId)
                workManager.getWorkInfoByIdLiveData(uuid).observeForever { workInfo ->
                    workInfo?.let { info ->
                        updateUiState(
                            workStatus = info.state,
                            isLoading = info.state == WorkInfo.State.RUNNING,
                            message = when (info.state) {
                                WorkInfo.State.RUNNING -> "Sincronizando..."
                                WorkInfo.State.SUCCEEDED -> "Sincronización completada"
                                WorkInfo.State.FAILED -> "Error en la sincronización"
                                WorkInfo.State.CANCELLED -> "Sincronización cancelada"
                                WorkInfo.State.BLOCKED -> "Sincronización bloqueada"
                                else -> "Preparando sincronización..."
                            },
                            progress = info.progress.getInt("progress", 0),
                            currentStep = info.progress.getString("current_step"),
                            outputData = info.outputData.getString("result")
                        )
                    }
                }
            } catch (e: Exception) {
                updateUiState(
                    isLoading = false,
                    message = "Error al observar estado del trabajo: ${e.message}",
                    workStatus = WorkInfo.State.FAILED
                )
            }
        }
    }

    private fun updateUiState(
        workStatus: WorkInfo.State = _uiState.value.workStatus,
        isLoading: Boolean = _uiState.value.isLoading,
        message: String = _uiState.value.message,
        progress: Int = _uiState.value.progress,
        currentStep: String? = _uiState.value.currentStep,
        outputData: String? = _uiState.value.outputData
    ) {
        _uiState.value = WorkUiState(
            workStatus = workStatus,
            isLoading = isLoading,
            message = message,
            progress = progress,
            currentStep = currentStep,
            outputData = outputData
        )
    }
}

// Workers de ejemplo que necesitarás implementar
class JuegosWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Simular progreso
            for (i in 0..100 step 20) {
                setProgress(workDataOf("progress" to i, "current_step" to "Sincronizando juegos... $i%"))
                kotlinx.coroutines.delay(500)
            }

            // Tu lógica de sincronización de juegos aquí

            Result.success(workDataOf("result" to "Juegos sincronizados correctamente"))
        } catch (e: Exception) {
            Result.failure(workDataOf("error" to e.message))
        }
    }
}

// Factory para crear WorkViewModel con dependencias
class WorkViewModelFactory(
    private val workManagerRepository: com.example.data.repository.WorkManagerRepository
) : androidx.lifecycle.ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WorkViewModel::class.java) -> {
                WorkViewModel(workManagerRepository.getWorkManager()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

class ReseñasWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Simular progreso
            for (i in 0..100 step 25) {
                setProgress(workDataOf("progress" to i, "current_step" to "Sincronizando reseñas... $i%"))
                kotlinx.coroutines.delay(400)
            }

            // Tu lógica de sincronización de reseñas aquí

            Result.success(workDataOf("result" to "Reseñas sincronizadas correctamente"))
        } catch (e: Exception) {
            Result.failure(workDataOf("error" to e.message))
        }
    }
}

class FullSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Sincronización completa
            setProgress(workDataOf("progress" to 0, "current_step" to "Iniciando sincronización completa..."))
            kotlinx.coroutines.delay(500)

            // Sincronizar juegos
            setProgress(workDataOf("progress" to 25, "current_step" to "Sincronizando juegos..."))
            kotlinx.coroutines.delay(1000)

            // Sincronizar reseñas
            setProgress(workDataOf("progress" to 75, "current_step" to "Sincronizando reseñas..."))
            kotlinx.coroutines.delay(1000)

            setProgress(workDataOf("progress" to 100, "current_step" to "Finalizando..."))
            kotlinx.coroutines.delay(500)

            Result.success(workDataOf("result" to "Sincronización completa exitosa"))
        } catch (e: Exception) {
            Result.failure(workDataOf("error" to e.message))
        }
    }
}