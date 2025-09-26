package com.example.catalogo.juegos

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.database.Juegos
import com.example.data.model.JuegosViewModel
import java.time.LocalDateTime

@Composable
fun AgregarJuegoScreen(
    navController: NavController,
    juegosViewModel: JuegosViewModel,
    usuarioId: Int
) {
    var titulo by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var archivoUri by remember { mutableStateOf<Uri?>(null) }

    // Selector de archivo
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> archivoUri = uri }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        TextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = genero,
            onValueChange = { genero = it },
            label = { Text("Género") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = calificacion,
            onValueChange = { calificacion = it },
            label = { Text("Calificación (0-10)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = { launcher.launch("*/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Seleccionar archivo")
        }

        archivoUri?.let {
            Text("Archivo seleccionado: ${it.lastPathSegment}")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val juego = Juegos(
                    titulo = titulo,
                    genero = genero,
                    calificacion = calificacion.toIntOrNull() ?: 0,
                    descripcion = descripcion,
                    fecha = LocalDateTime.now(),
                    colaboradorId = usuarioId,
                    archivoUri = archivoUri?.toString()
                )
                juegosViewModel.addJuego(juego)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Juego")
        }
    }
}
