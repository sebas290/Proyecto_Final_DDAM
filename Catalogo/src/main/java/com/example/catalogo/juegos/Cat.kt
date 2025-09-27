package com.example.catalogo.juegos

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.ClasesRelacionales.JuegoConUsuario
import com.example.data.model.JuegosViewModel
import android.widget.Toast
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaJuegosScreen(
    navController: NavController,
    viewModel: JuegosViewModel,
    onChatClick: (Int) -> Unit,
    usuarioId: Int
) {
    val juegos by viewModel.juegos.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<JuegoConUsuario?>(null) }
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    var deletedJuego by remember { mutableStateOf<JuegoConUsuario?>(null) }

    // Lógica para deshacer eliminación
    deletedJuego?.let { juegoConUsuario ->
        LaunchedEffect(juegoConUsuario) {
            val result = snackBarHostState.showSnackbar(
                message = "Juego eliminado",
                actionLabel = "Deshacer",
                duration = SnackbarDuration.Long
            )

            if (result == SnackbarResult.ActionPerformed) {
                viewModel.addJuego(juegoConUsuario.juego)
                deletedJuego = null
            } else {
                deletedJuego = null
            }
        }
    }

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
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("agregarJuego/$usuarioId") }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Juego")
            }
        }
    ) { padding ->
        if (juegos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay juegos disponibles")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(
                    items = juegos,
                    key = { it.juego.id }
                ) { juegoConUsuario ->
                    SwipeableJuegoCard(
                        juegoConUsuario = juegoConUsuario,
                        usuarioId = usuarioId,
                        onEdit = { juego ->
                            navController.navigate("agregarJuego/$usuarioId/${juego.id}")
                        },
                        onDelete = { juegoConUsuario ->
                            viewModel.deleteJuego(juegoConUsuario.juego)
                            deletedJuego = juegoConUsuario
                        },
                        onClick = { juego ->
                            // Temporalmente comentado hasta crear la pantalla de detalle
                            // navController.navigate("detalleJuego/${juego.id}")
                            Toast.makeText(context, "Clic en ${juego.titulo}", Toast.LENGTH_SHORT).show()
                        },
                        onAddReview = { juego ->
                            navController.navigate("agregarReseña/${juego.id}/$usuarioId")
                        },
                        onViewReviews = { juego ->
                            navController.navigate("listaResenas/${juego.id}")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableJuegoCard(
    juegoConUsuario: JuegoConUsuario,
    usuarioId: Int,
    onEdit: (com.example.data.database.Juegos) -> Unit,
    onDelete: (JuegoConUsuario) -> Unit,
    onClick: (com.example.data.database.Juegos) -> Unit,
    onAddReview: (com.example.data.database.Juegos) -> Unit,
    onViewReviews: (com.example.data.database.Juegos) -> Unit
) {
    val juego = juegoConUsuario.juego
    val isOwner = juego.colaboradorId == usuarioId
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        if (isOwner) {
            // Card con swipe solo para propietarios
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    when (dismissValue) {
                        SwipeToDismissBoxValue.EndToStart -> {
                            // Swipe hacia la izquierda - Eliminar
                            visible = false
                            onDelete(juegoConUsuario)
                            true
                        }
                        SwipeToDismissBoxValue.StartToEnd -> {
                            // Swipe hacia la derecha - Editar
                            onEdit(juego)
                            false
                        }
                        SwipeToDismissBoxValue.Settled -> false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    SwipeBackground(dismissState)
                },
                content = {
                    JuegoCard(
                        juegoConUsuario = juegoConUsuario,
                        isOwner = isOwner,
                        onClick = onClick,
                        onAddReview = onAddReview,
                        onViewReviews = onViewReviews
                    )
                }
            )
        } else {
            // Card normal sin swipe
            JuegoCard(
                juegoConUsuario = juegoConUsuario,
                isOwner = isOwner,
                onClick = onClick,
                onAddReview = onAddReview,
                onViewReviews = onViewReviews
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Color(0xFFF44336) // Rojo para eliminar
        SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50) // Verde para editar
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }

    val alignment = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.Settled -> Alignment.Center
    }

    val icon = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
        SwipeToDismissBoxValue.Settled -> Icons.Default.Delete
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        if (color != Color.Transparent) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun JuegoCard(
    juegoConUsuario: JuegoConUsuario,
    isOwner: Boolean,
    onClick: (com.example.data.database.Juegos) -> Unit,
    onAddReview: (com.example.data.database.Juegos) -> Unit,
    onViewReviews: (com.example.data.database.Juegos) -> Unit
) {
    val juego = juegoConUsuario.juego
    val usuario = juegoConUsuario.usuario

    // Estado para detectar si está siendo presionado
    var isPressed by remember { mutableStateOf(false) }

    // Animación de escala basada en si está presionado
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "CardPressScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // Se presiona - hacer grande
                        isPressed = true

                        // Esperar hasta que se suelte
                        tryAwaitRelease()

                        // Se soltó - volver al tamaño normal
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPressed) 8.dp else 4.dp,
            pressedElevation = 12.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header del juego
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = juego.titulo,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "por ${usuario.alias}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isOwner) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Tuyo",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Información del juego
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = juego.genero,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Calificación",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFD700)
                    )
                    Text(
                        text = "${juego.calificacion}/10",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = juego.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onAddReview(juego) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Reseñar", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Button(
                    onClick = { onViewReviews(juego) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = "Ver",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Ver", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            // Hint para swipe si es el propietario
            if (isOwner) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Desliza → para editar o ← para eliminar",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}