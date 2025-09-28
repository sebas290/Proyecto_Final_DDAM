package com.example.catalogo.juegos

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.database.Juegos
import com.example.data.model.JuegosViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarJuegoScreen(
    navController: NavController,
    juegosViewModel: JuegosViewModel,
    usuarioId: Int,
    juegoId: Int? = null // null = agregar, valor = editar
) {
    val context = LocalContext.current
    val isEditing = juegoId != null
    val juegoExistente = if (isEditing) juegosViewModel.getJuegoById(juegoId!!)?.juego else null

    // Verificar permisos para edición
    if (isEditing && (juegoExistente == null || juegoExistente.colaboradorId != usuarioId)) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "No tienes permisos para editar este juego", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        return
    }

    var titulo by remember { mutableStateOf(juegoExistente?.titulo ?: "") }
    var genero by remember { mutableStateOf(juegoExistente?.genero ?: "") }
    var descripcion by remember { mutableStateOf(juegoExistente?.descripcion ?: "") }
    var archivoUri by remember { mutableStateOf<Uri?>(juegoExistente?.archivoUri?.let { Uri.parse(it) }) }
    var isLoading by remember { mutableStateOf(false) }

    // Selector de archivo
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> archivoUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Juego" else "Agregar Juego") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            TextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(Modifier.height(8.dp))

            TextField(
                value = genero,
                onValueChange = { genero = it },
                label = { Text("Género") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(Modifier.height(8.dp))

            TextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !isLoading
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { launcher.launch("*/*") }, // FIXED: Changed from "/" to "*/*"
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isEditing) "Cambiar archivo" else "Seleccionar archivo")
            }

            archivoUri?.let {
                Text(
                    text = "Archivo: ${it.lastPathSegment ?: "Seleccionado"}",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (titulo.isBlank() || genero.isBlank() || descripcion.isBlank()) {
                        Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    if (isEditing && juegoExistente != null) {
                        // Actualizar juego existente
                        val juegoActualizado = juegoExistente.copy(
                            titulo = titulo,
                            genero = genero,
                            descripcion = descripcion,
                            archivoUri = archivoUri?.toString()
                        )
                        juegosViewModel.updateJuego(juegoActualizado)
                        Toast.makeText(context, "Juego actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    } else {
                        // Crear nuevo juego
                        val juego = Juegos(
                            titulo = titulo,
                            genero = genero,
                            descripcion = descripcion,
                            fecha = LocalDateTime.now(),
                            colaboradorId = usuarioId,
                            archivoUri = archivoUri?.toString()
                        )
                        juegosViewModel.addJuego(juego)
                        Toast.makeText(context, "Juego creado exitosamente", Toast.LENGTH_SHORT).show()
                    }

                    isLoading = false
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (isEditing) "Actualizando..." else "Creando...")
                } else {
                    Text(if (isEditing) "Guardar Cambios" else "Agregar Juego")
                }
            }
        }
    }
}