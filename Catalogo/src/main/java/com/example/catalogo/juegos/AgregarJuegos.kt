package com.example.catalogo.juegos

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.database.Juegos
import com.example.data.model.JuegosViewModel
import java.time.LocalDateTime
import com.example.data.firebase.addJuegoToFirestore

@Composable
fun AgregarJuegoScreen(
    navController: NavController,
    juegosViewModel: JuegosViewModel,
    usuarioId: Int
) {
    var titulo by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var archivoUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Selector de archivo
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> archivoUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

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
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { launcher.launch("*/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
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
                    descripcion = descripcion,
                    fecha = LocalDateTime.now(),
                    colaboradorId = usuarioId,
                    archivoUri = archivoUri?.toString()
                )

                // Guardar en Room
                juegosViewModel.addJuego(juego)

                // Guardar en Firestore
                addJuegoToFirestore(juego) { success, msg ->
                    if (success) {
                        Toast.makeText(context, "Juego agregado en Firebase", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error Firebase: $msg", Toast.LENGTH_LONG).show()
                    }
                }

                // Volver atrás
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Juego")
        }
    }
}
