package com.example.catalogo.juegos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.model.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaReseñasScreen(
    navController: NavController,
    reviewViewModel: ReviewViewModel,
    juegoId: Int,
    usuarioId: Int = 0
) {
    val reseñas by reviewViewModel.reseñasConUsuario.collectAsState()

    LaunchedEffect(juegoId) {
        reviewViewModel.getReviewPorJuego(juegoId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            if (reseñas.isEmpty()) {
                item {
                    Text(
                        text = "Todavía no hay reseñas para este juego",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(reseñas) { reseñaConUsuario ->
                    val reseña = reseñaConUsuario.reseña
                    val usuario = reseñaConUsuario.usuario

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Usuario: ${usuario.alias}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = "Calificación: ${reseña.estrellas}/10")
                            Text(text = reseña.comentario ?: "")
                            Text(text = "Fecha: ${reseña.fecha}")
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("listaJuegos/$usuarioId") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.List, contentDescription = "Ir al catálogo")
        }
    }
}