package com.example.catalogo.reseñas // ajusta el package si lo tenías distinto

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.database.Reseña
import com.example.data.model.ReviewViewModel
import java.time.LocalDateTime

@Composable
fun AgregarReseñaScreen(
    navController: NavController,
    reviewViewModel: ReviewViewModel,
    juegoId: Int,
    usuarioId: Int
) {
    val ctx = LocalContext.current
    var estrellasFloat by remember { mutableStateOf(5f) } // slider 0..10
    var comentario by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(text = "Agregar reseña", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Puntuación: ${estrellasFloat.toInt()}/10")
        Slider(
            value = estrellasFloat,
            onValueChange = { estrellasFloat = it },
            valueRange = 0f..10f,
            steps = 9,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = comentario,
            onValueChange = { comentario = it },
            label = { Text("Comentario (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            Log.d("DEBUG_RESEÑA", "Intentando agregar reseña -> juegoId=$juegoId, usuarioId=$usuarioId")

            // Validaciones básicas antes de insertar
            if (juegoId <= 0) {
                Toast.makeText(ctx, "Id de juego inválido: $juegoId", Toast.LENGTH_LONG).show()
                return@Button
            }
            if (usuarioId <= 0) {
                Toast.makeText(ctx, "Id de usuario inválido: $usuarioId", Toast.LENGTH_LONG).show()
                return@Button
            }

            val reseña = Reseña(
                usuarioId = usuarioId,
                videojuegoId = juegoId,
                estrellas = estrellasFloat.toInt(),
                comentario = if (comentario.isBlank()) null else comentario,
                fecha = LocalDateTime.now()
            )

            // llamar al ViewModel (manejo de errores dentro del ViewModel)
            reviewViewModel.addReseña(reseña)

            // Volver a la lista de reseñas del juego
            navController.navigate("listaResenas/$juegoId") {
                // opcional: evitar stack duplicado
                popUpTo("listaResenas/$juegoId") { inclusive = true }
            }

        }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar reseña")
        }
    }
}
