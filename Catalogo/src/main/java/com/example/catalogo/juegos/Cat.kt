package com.example.catalogo.juegos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.model.JuegosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaJuegosScreen(
    navController: NavController,
    viewModel: JuegosViewModel,
    onChatClick: (Int) -> Unit,
    usuarioId: Int
) {
    val juegos by viewModel.juegos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Juegos") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("agregarJuego/$usuarioId") }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(juegos) { juegoConUsuario ->
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
                            Button(onClick = { navController.navigate("agregarReseña/${juego.id}/$usuarioId") }) {
                                Text("Agregar reseña")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { navController.navigate("listaResenas/${juego.id}") }) {
                                Text("Ver reseñas")
                            }
                        }
                    }
                }
            }
        }
    }
}