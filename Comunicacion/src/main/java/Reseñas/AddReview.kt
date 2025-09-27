package com.example.catalogo.reseñas

import Filtro.CalificacionHelper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.database.Reseña
import com.example.data.model.ReviewViewModel
import com.example.data.model.JuegosViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarReseñaScreen(
    navController: NavController,
    reviewViewModel: ReviewViewModel,
    juegosViewModel: JuegosViewModel,
    juegoId: Int,
    usuarioId: Int
) {
    val ctx = LocalContext.current
    var estrellas by remember { mutableStateOf(0) }
    var comentario by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Reseña") },
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
            Text(
                text = "¿Qué te pareció este juego?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Califica tu experiencia del 1 al 10",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            StarRatingBar(
                rating = estrellas,
                onRatingChanged = { newRating ->
                    estrellas = newRating
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Comparte tu experiencia (opcional)") },
                placeholder = { Text("Cuéntanos qué te gustó o no te gustó...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (estrellas <= 0) {
                        Toast.makeText(ctx, "Por favor selecciona una calificación", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    Log.d("DEBUG_RESEÑA", "Intentando agregar reseña -> juegoId=$juegoId, usuarioId=$usuarioId")

                    if (juegoId <= 0) {
                        Toast.makeText(ctx, "Id de juego inválido: $juegoId", Toast.LENGTH_LONG).show()
                        isLoading = false
                        return@Button
                    }
                    if (usuarioId <= 0) {
                        Toast.makeText(ctx, "Id de usuario inválido: $usuarioId", Toast.LENGTH_LONG).show()
                        isLoading = false
                        return@Button
                    }

                    val reseña = Reseña(
                        usuarioId = usuarioId,
                        videojuegoId = juegoId,
                        estrellas = estrellas,
                        comentario = if (comentario.isBlank()) null else comentario,
                        fecha = LocalDateTime.now()
                    )

                    // Guardar en base de datos local
                    reviewViewModel.addReseña(reseña)

                    // Actualizar calificación del juego automáticamente
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            CalificacionHelper.actualizarCalificacionJuego(
                                juegoId = juegoId,
                                juegosViewModel = juegosViewModel,
                                reviewViewModel = reviewViewModel
                            )
                        } catch (e: Exception) {
                            Log.e("CalificacionUpdate", "Error actualizando calificación: ${e.message}")
                        }
                    }

                    // Guardar en Firestore
                    addReseñaToFirestore(reseña) { success, msg ->
                        if (success) {
                            Toast.makeText(ctx, "Reseña guardada en Firebase", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(ctx, "Error Firebase: $msg", Toast.LENGTH_LONG).show()
                        }
                    }

                    Toast.makeText(ctx, "Reseña agregada exitosamente", Toast.LENGTH_SHORT).show()

                    navController.navigate("listaJuegos/$usuarioId") {
                        popUpTo("listaJuegos/$usuarioId") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && estrellas > 0
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Text("Guardar Reseña")
                }
            }

            if (estrellas > 0) {
                Text(
                    text = "Calificación: $estrellas/10",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Función para guardar reseña en Firestore
private fun addReseñaToFirestore(
    reseña: Reseña,
    callback: (Boolean, String) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    val reseñaMap = mapOf(
        "usuarioId" to reseña.usuarioId,
        "videojuegoId" to reseña.videojuegoId,
        "estrellas" to reseña.estrellas,
        "comentario" to reseña.comentario,
        "fecha" to com.google.firebase.Timestamp.now()
    )

    firestore.collection("reseñas")
        .add(reseñaMap)
        .addOnSuccessListener {
            callback(true, "Reseña guardada exitosamente")
        }
        .addOnFailureListener { e ->
            callback(false, e.message ?: "Error desconocido")
        }
}

@Composable
fun StarRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    maxStars: Int = 10
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = rating,
            transitionSpec = {
                slideInVertically { height -> height } + fadeIn() togetherWith
                        slideOutVertically { height -> -height } + fadeOut()
            },
            label = "rating_number"
        ) { currentRating ->
            Text(
                text = if (currentRating == 0) "Sin calificar" else "$currentRating/10",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    currentRating == 0 -> MaterialTheme.colorScheme.onSurfaceVariant
                    currentRating <= 3 -> Color(0xFFE53E3E)
                    currentRating <= 6 -> Color(0xFFFF8C00)
                    currentRating <= 8 -> Color(0xFFFFD700)
                    else -> Color(0xFF38A169)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    val starNumber = index + 1
                    StarButton(
                        starNumber = starNumber,
                        isSelected = starNumber <= rating,
                        onClick = { onRatingChanged(starNumber) }
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    val starNumber = index + 6
                    StarButton(
                        starNumber = starNumber,
                        isSelected = starNumber <= rating,
                        onClick = { onRatingChanged(starNumber) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedContent(
            targetState = rating,
            label = "description"
        ) { currentRating ->
            Text(
                text = when {
                    currentRating == 0 -> "Toca las estrellas para calificar"
                    currentRating <= 2 -> "Muy malo"
                    currentRating <= 4 -> "Malo"
                    currentRating <= 6 -> "Regular"
                    currentRating <= 8 -> "Bueno"
                    currentRating <= 9 -> "Excelente"
                    else -> "Increíble"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StarButton(
    starNumber: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.8f
            isSelected -> 1.2f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "star_scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "star_rotation"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else Color.Transparent
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            }
            .scale(scale)
            .graphicsLayer {
                rotationZ = if (isSelected) rotation else 0f
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = starNumber.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Icon(
            imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
            contentDescription = "Estrella $starNumber",
            modifier = Modifier.size(32.dp),
            tint = if (isSelected) Color(0xFFFFD700) else Color.Gray
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}