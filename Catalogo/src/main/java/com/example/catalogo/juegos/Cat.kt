package com.example.catalogo.juegos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.database.Juegos
import com.example.data.model.JuegosViewModel

@Composable
fun ListaJuegosScreen(
    navController: NavController,
    viewModel: JuegosViewModel,
    onChatClick: () -> Unit,
    usuarioId: Int
) {
    val juegos by viewModel.juegos.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("agregarJuego/$usuarioId") }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            juegos.forEach { juegoConUsuario ->
                val juego = juegoConUsuario.juego
                val usuario = juegoConUsuario.usuario

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate("detalleJuego/${juego.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = juego.titulo, style = MaterialTheme.typography.titleLarge)
                        Text(text = "Publicado por: ${usuario.alias}")
                        Text(text = "Género: ${juego.genero}")
                        Text(text = "Calificación: ${juego.calificacion}/10")
                        Text(text = "Descripción: ${juego.descripcion}")
                        Text(text = "Fecha: ${juego.fecha}")

                        Row {
                            Button(onClick = onChatClick) {
                                Text("Agregar una reseña")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = onChatClick) {
                                Text("Ver las reseñas")
                            }
                        }
                    }
                }
            }
        }
    }
}

