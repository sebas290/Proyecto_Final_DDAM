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
import com.example.data.database.Juegos

@Composable
fun ListaJuegosScreen(navController: NavController) {
    val juegos = listOf(
        Juegos(
            id = 1,
            titulo = "Zelda",
            genero = "Aventura",
            calificacion = 10,
            colaboradorId = "id_del_colaborador_123",
            descripcion = "Una gran aventura épica",
            fecha = "2023-10-26"
        ),
        Juegos(
            id = 2,
            titulo = "Halo",
            genero = "Shooter",
            calificacion = 9,
            colaboradorId = "id_del_colaborador_456",
            descripcion = "Juego de disparos de ciencia ficción",
            fecha = "2023-10-26"
        )
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
                        Text(text = juego.titulo, style = MaterialTheme.typography.titleLarge)
                        Text(text = "Género: ${juego.genero}")
                        Text(text = "Calificación: ${juego.calificacion}/10")

                        // Añade las propiedades faltantes
                        Text(text = "Descripción: ${juego.descripcion}")
                        Text(text = "Fecha: ${juego.fecha}")
                        Text(text = "Colaborador ID: ${juego.colaboradorId}")
                    }
                }
            }
        }
    }
}