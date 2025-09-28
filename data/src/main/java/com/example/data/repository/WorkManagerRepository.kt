package com.example.data.repository

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.*
import com.example.data.database.SyncDataWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class WorkManagerRepository(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    // Método para obtener la instancia de WorkManager (necesario para WorkViewModel)
    fun getWorkManager(): WorkManager = workManager

    // Trabajo único (una vez)
    fun startOneTimeWork(inputData: String) {
        val inputDataBuilder = Data.Builder()
            .putString(SyncDataWorker.KEY_INPUT_DATA, inputData)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncDataWorker>()
            .setInputData(inputDataBuilder)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED) // Requiere internet
                    .setRequiresBatteryNotLow(true) // Batería no baja
                    .setRequiresStorageNotLow(true) // Almacenamiento no bajo
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR, // Política de reintento
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            SyncDataWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE, // Reemplaza si ya existe
            workRequest
        )
    }

    // Trabajo periódico (se repite)
    fun startPeriodicWork() {
        val workRequest = PeriodicWorkRequestBuilder<SyncDataWorker>(
            15, TimeUnit.MINUTES // Cada 15 minutos (mínimo permitido)
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "${SyncDataWorker.WORK_NAME}_periodic",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    // Trabajo con retraso
    fun startDelayedWork(delayMinutes: Long) {
        val workRequest = OneTimeWorkRequestBuilder<SyncDataWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()

        workManager.enqueue(workRequest)
    }

    // Observar estado del trabajo
    fun observeWorkStatus(): Flow<WorkInfo.State?> {
        return workManager.getWorkInfosForUniqueWorkLiveData(SyncDataWorker.WORK_NAME)
            .asFlow()
            .map { workInfos ->
                workInfos.firstOrNull()?.state
            }
    }

    // Observar progreso del trabajo
    fun observeWorkProgress(): Flow<Int> {
        return workManager.getWorkInfosForUniqueWorkLiveData(SyncDataWorker.WORK_NAME)
            .asFlow()
            .map { workInfos ->
                workInfos.firstOrNull()?.progress?.getInt("progress", 0) ?: 0
            }
    }

    // Observar paso actual del trabajo
    fun observeCurrentStep(): Flow<String?> {
        return workManager.getWorkInfosForUniqueWorkLiveData(SyncDataWorker.WORK_NAME)
            .asFlow()
            .map { workInfos ->
                workInfos.firstOrNull()?.progress?.getString("current_step")
            }
    }

    // Observar datos de salida del trabajo
    fun observeWorkOutput(): Flow<String?> {
        return workManager.getWorkInfosForUniqueWorkLiveData(SyncDataWorker.WORK_NAME)
            .asFlow()
            .map { workInfos ->
                workInfos.firstOrNull()?.outputData?.getString(SyncDataWorker.KEY_OUTPUT_DATA)
            }
    }

    // Cancelar trabajo específico
    fun cancelWork() {
        workManager.cancelUniqueWork(SyncDataWorker.WORK_NAME)
    }

    // Cancelar trabajo periódico
    fun cancelPeriodicWork() {
        workManager.cancelUniqueWork("${SyncDataWorker.WORK_NAME}_periodic")
    }

    // Cancelar todos los trabajos
    fun cancelAllWork() {
        workManager.cancelAllWork()
    }

    // Obtener información básica del trabajo (sin usar await)
    fun getWorkInfoFlow(): Flow<WorkInfo?> {
        return workManager.getWorkInfosForUniqueWorkLiveData(SyncDataWorker.WORK_NAME)
            .asFlow()
            .map { workInfos ->
                workInfos.firstOrNull()
            }
    }

    // Función para verificar si hay trabajos en ejecución
    fun hasRunningWork(): Flow<Boolean> {
        return workManager.getWorkInfosForUniqueWorkLiveData(SyncDataWorker.WORK_NAME)
            .asFlow()
            .map { workInfos ->
                workInfos.any { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED }
            }
    }
}