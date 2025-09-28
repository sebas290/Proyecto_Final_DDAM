package com.example.catalogo.juegos

import Filtro.CalificacionHelper
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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

    // Variables para filtros
    var showFilterDialog by remember { mutableStateOf(false) }
    var filtroCalificacionMin by remember { mutableFloatStateOf(0f) }
    var filtroCalificacionMax by remember { mutableFloatStateOf(10f) }
    var juegosFiltrados by remember { mutableStateOf(juegos) }

    // Aplicar filtros cuando cambian los juegos o los filtros
    LaunchedEffect(juegos, filtroCalificacionMin, filtroCalificacionMax) {
        juegosFiltrados = if (filtroCalificacionMin == 0f && filtroCalificacionMax == 10f) {
            juegos
        } else {
            CalificacionHelper.filtrarJuegosPorCalificacion(
                juegos = juegos,
                calificacionMinima = filtroCalificacionMin.toDouble(),
                calificacionMaxima = filtroCalificacionMax.toDouble()
            )
        }
    }

    // 🔥 LÓGICA CORREGIDA PARA DESHACER CON RESEÑAS
    deletedJuego?.let { juegoConUsuario ->
        LaunchedEffect(juegoConUsuario) {
            val result = snackBarHostState.showSnackbar(
                message = "Juego eliminado",
                actionLabel = "Deshacer",
                duration = SnackbarDuration.Long
            )

            if (result == SnackbarResult.ActionPerformed) {
                // 🔥 USAR LA NUEVA FUNCIÓN QUE RESTAURA JUEGO + RESEÑAS
                viewModel.restaurarJuegoConReseñas(juegoConUsuario.juego)
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
                    // Botón de filtros
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Filtrar",
                            tint = if (filtroCalificacionMin > 0f || filtroCalificacionMax < 10f)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Nuevo icono de perfil
                    IconButton(onClick = { navController.navigate("perfil/$usuarioId") }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
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
            Column(modifier = Modifier.padding(padding)) {
                // Mostrar información si hay filtros activos
                if (filtroCalificacionMin > 0f || filtroCalificacionMax < 10f) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filtrado: ${filtroCalificacionMin.toInt()}-${filtroCalificacionMax.toInt()}/10",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            TextButton(
                                onClick = {
                                    filtroCalificacionMin = 0f
                                    filtroCalificacionMax = 10f
                                }
                            ) {
                                Text("Limpiar")
                            }
                        }
                    }
                }

                if (juegosFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No hay juegos con esa calificación")
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = {
                                filtroCalificacionMin = 0f
                                filtroCalificacionMax = 10f
                            }) {
                                Text("Limpiar filtros")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(
                            items = juegosFiltrados,
                            key = { it.juego.id }
                        ) { juegoConUsuario ->
                            SwipeableJuegoCard(
                                juegoConUsuario = juegoConUsuario,
                                usuarioId = usuarioId,
                                onEdit = { juego ->
                                    navController.navigate("agregarJuego/$usuarioId/${juego.id}")
                                },
                                onDelete = { juegoConUsuario ->
                                    // 🔥 USAR deleteJuego que guarda las reseñas antes de eliminar
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
                                    navController.navigate("listaResenas/${juego.id}/$usuarioId")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de filtros con estrellas interactivas
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Filtrar por calificación")
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Calificación mínima",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Estrellas para calificación mínima
                    InteractiveStarRating(
                        rating = filtroCalificacionMin.toInt(),
                        onRatingChanged = { newRating ->
                            filtroCalificacionMin = newRating.toFloat()
                            // Asegurar que min no sea mayor que max
                            if (filtroCalificacionMin > filtroCalificacionMax) {
                                filtroCalificacionMax = filtroCalificacionMin
                            }
                        }
                    )

                    Text(
                        "${filtroCalificacionMin.toInt()}/10",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Calificación máxima",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Estrellas para calificación máxima
                    InteractiveStarRating(
                        rating = filtroCalificacionMax.toInt(),
                        onRatingChanged = { newRating ->
                            filtroCalificacionMax = newRating.toFloat()
                            // Asegurar que max no sea menor que min
                            if (filtroCalificacionMax < filtroCalificacionMin) {
                                filtroCalificacionMin = filtroCalificacionMax
                            }
                        }
                    )

                    Text(
                        "${filtroCalificacionMax.toInt()}/10",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (filtroCalificacionMin == filtroCalificacionMax) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Mostrando solo juegos con ${filtroCalificacionMin.toInt()}/10",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Aplicar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    filtroCalificacionMin = 0f
                    filtroCalificacionMax = 10f
                    showFilterDialog = false
                }) {
                    Text("Limpiar")
                }
            }
        )
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

@Composable
fun InteractiveStarRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    maxRating: Int = 10,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxRating) {
            val isSelected = i <= rating

            Icon(
                imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Estrella $i",
                tint = if (isSelected) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(28.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onRatingChanged(i)
                            }
                        )
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Color(0xFFF44336)
        SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50)
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
    val context = LocalContext.current

    // Estados de animación
    var isExpanded by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    // Animación de escala basada en si está presionado
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "CardPressScale"
    )

    // Animación de expansión
    val expandedHeight by animateDpAsState(
        targetValue = if (isExpanded) 420.dp else 200.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CardExpansion"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = expandedHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // Se presiona - hacer ligeramente más pequeño
                        isPressed = true

                        // Esperar hasta que se suelte
                        tryAwaitRelease()

                        // Se soltó - volver al tamaño normal
                        isPressed = false
                    },
                    onTap = {
                        // Toggle expansión
                        isExpanded = !isExpanded
                        // También ejecutar el onClick original si no está expandido
                        if (!isExpanded) {
                            onClick(juego)
                        }
                    }
                )
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 8.dp else 4.dp,
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
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Contenido adicional cuando está expandido
            AnimatedVisibility(
                visible = isExpanded,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Información adicional del juego
                    Text(
                        text = "Detalles del juego",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Colaborador",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = usuario.alias,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "ID del juego",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "#${juego.id}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Mostrar archivo si existe
                    juego.archivoUri?.let { archivoUri ->
                        Spacer(Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Archivo adjunto",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = archivoUri.substringAfterLast("/").ifEmpty { "Archivo disponible" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        // Aquí puedes implementar la lógica para abrir el archivo
                                        // Por ejemplo, usando un Intent para abrir el archivo
                                        Toast.makeText(
                                            context,
                                            "Abrir archivo: ${archivoUri.substringAfterLast("/")}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.size(width = 80.dp, height = 32.dp),
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = "Abrir archivo",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Indicador de toque para cerrar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ExpandLess,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Toca para cerrar",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

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

            // Hint para swipe si es el propietario y botón de expansión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isOwner) {
                    Text(
                        text = "Desliza → editar, ← eliminar",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Indicador de expansión
                if (!isExpanded) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Ver más",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            Icons.Default.ExpandMore,
                            contentDescription = "Expandir",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}