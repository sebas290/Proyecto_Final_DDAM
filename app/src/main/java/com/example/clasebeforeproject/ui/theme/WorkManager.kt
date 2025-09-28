package com.example.clasebeforeproject.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.work.WorkInfo
import com.example.data.model.WorkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkManagerScreen(
    workViewModel: WorkViewModel,
    onBack: () -> Unit = {}
) {
    val workState by workViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sincronización de Datos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        // Estado actual
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (workState.workStatus) {
                    WorkInfo.State.RUNNING -> MaterialTheme.colorScheme.primaryContainer
                    WorkInfo.State.SUCCEEDED -> MaterialTheme.colorScheme.secondaryContainer
                    WorkInfo.State.FAILED -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Estado de Sincronización",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = workState.message,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (workState.isLoading) {
                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = workState.progress / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Progreso: ${workState.progress}%",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    workState.currentStep?.let { step ->
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                workState.outputData?.let { output ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Resultado: $output",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Botones de sincronización manual
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Sincronización Manual",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { workViewModel.syncJuegosData() },
                        modifier = Modifier.weight(1f),
                        enabled = !workState.isLoading
                    ) {
                        Text("Sincronizar Juegos")
                    }

                    Button(
                        onClick = { workViewModel.syncReseñasData() },
                        modifier = Modifier.weight(1f),
                        enabled = !workState.isLoading
                    ) {
                        Text("Sincronizar Reseñas")
                    }
                }

                Button(
                    onClick = { workViewModel.fullSync() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !workState.isLoading
                ) {
                    Text("Sincronización Completa")
                }
            }
        }

        // Sincronización automática
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Sincronización Automática",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { workViewModel.enableAutoSync() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Activar Sincronización Automática (cada 15 min)")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { workViewModel.scheduledSync(5) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("En 5 min")
                    }

                    Button(
                        onClick = { workViewModel.scheduledSync(30) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("En 30 min")
                    }
                }
            }
        }

        // Controles
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Controles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { workViewModel.cancelSync() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancelar")
                    }

                    OutlinedButton(
                        onClick = { workViewModel.cancelAllWork() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar Todo")
                    }
                }
            }
        }
    }
}