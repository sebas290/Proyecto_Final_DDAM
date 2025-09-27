package com.example.catalogo.juegos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.model.ReviewViewModel

@Composable
fun ListaReseñasScreen(
    navController: NavController,
    reviewViewModel: ReviewViewModel,
    juegoId: Int
) {
    val reseñas by reviewViewModel.reseñasConUsuario.collectAsState()

    LaunchedEffect(juegoId) {
        reviewViewModel.getReviewPorJuego(juegoId)
    }

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
}
