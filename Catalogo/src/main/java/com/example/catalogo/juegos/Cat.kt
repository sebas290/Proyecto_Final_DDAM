package com.example.catalogo.juegos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.database.Juego

@Composable
fun ListaJuegosScreen(navController: NavController) {
    val juegos = listOf(
        Juego(1, "Zelda", "Aventura", 10),
        Juego(2, "Halo", "Shooter", 9)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("agregarJuego") }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            juegos.forEach { juego ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate("detalleJuego/${juego.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = juego.nombre, style = MaterialTheme.typography.titleLarge)
                        Text(text = "Género: ${juego.genero}")
                        Text(text = "Calificación: ${juego.calificacion}/10")
                    }
                }
            }
        }
    }
}

