package login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.model.UsuariosViewModel
import com.example.data.database.Usuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    usuariosViewModel: UsuariosViewModel,
    auth: FirebaseAuth,
    usuarioId: Int
) {
    // Estados para la UI
    var usuario by remember { mutableStateOf<Usuarios?>(null) }
    var nuevoAlias by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingUser by remember { mutableStateOf(true) }
    var aliasError by remember { mutableStateOf("") }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    // Cargar usuario al inicializar usando corrutina
    LaunchedEffect(usuarioId) {
        isLoadingUser = true
        try {
            // Usar el nuevo método suspend del ViewModel
            val usuarioEncontrado = usuariosViewModel.getUsuarioByIdSuspend(usuarioId)
            usuario = usuarioEncontrado
            usuario?.let {
                nuevoAlias = it.alias
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoadingUser = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLoadingUser) {
            // Mostrar loading mientras carga el usuario
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Cargando perfil...")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // Icono de perfil grande
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = RoundedCornerShape(60.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Información del usuario
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Información del Usuario",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Email (solo lectura)
                        Column {
                            Text(
                                text = "Email",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = usuario?.correo ?: "No disponible",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Alias (editable)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Alias",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                IconButton(
                                    onClick = {
                                        isEditing = !isEditing
                                        if (!isEditing && nuevoAlias != usuario?.alias) {
                                            // Cancelar cambios
                                            nuevoAlias = usuario?.alias ?: ""
                                            aliasError = ""
                                        }
                                    }
                                ) {
                                    Icon(
                                        if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                                        contentDescription = if (isEditing) "Cancelar" else "Editar"
                                    )
                                }
                            }

                            if (isEditing) {
                                OutlinedTextField(
                                    value = nuevoAlias,
                                    onValueChange = {
                                        if (it.length <= 10) {
                                            nuevoAlias = it
                                            aliasError = ""
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Ingresa tu nuevo alias") },
                                    singleLine = true,
                                    isError = aliasError.isNotEmpty(),
                                    supportingText = if (aliasError.isNotEmpty()) {
                                        { Text(aliasError, color = MaterialTheme.colorScheme.error) }
                                    } else null
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            isEditing = false
                                            nuevoAlias = usuario?.alias ?: ""
                                            aliasError = ""
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Cancelar")
                                    }

                                    Button(
                                        onClick = {
                                            if (nuevoAlias.isNotBlank() && nuevoAlias.trim().isNotEmpty()) {
                                                isLoading = true
                                                scope.launch {
                                                    try {
                                                        // Verificar que el alias no esté en uso por otro usuario
                                                        val existenteConAlias = usuariosViewModel.getUsuarioByAlias(nuevoAlias.trim())
                                                        if (existenteConAlias != null && existenteConAlias.id != usuarioId) {
                                                            aliasError = "Este alias ya está en uso"
                                                            isLoading = false
                                                            return@launch
                                                        }

                                                        aliasError = ""
                                                        usuario?.let { usuarioActual ->
                                                            val usuarioActualizado = usuarioActual.copy(alias = nuevoAlias.trim())
                                                            val success = usuariosViewModel.updateUsuario(usuarioActualizado)

                                                            if (success) {
                                                                // Actualizar también en Firestore si el usuario usa Firebase
                                                                val uid = auth.currentUser?.uid
                                                                if (uid != null) {
                                                                    try {
                                                                        firestore.collection("usuarios").document(uid)
                                                                            .update("alias", nuevoAlias.trim())
                                                                            .addOnSuccessListener {
                                                                                Toast.makeText(context, "Alias actualizado en Firestore", Toast.LENGTH_SHORT).show()
                                                                            }
                                                                            .addOnFailureListener { e ->
                                                                                Toast.makeText(context, "Error al actualizar en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                                                            }
                                                                    } catch (e: Exception) {
                                                                        Toast.makeText(context, "Error Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }

                                                                usuario = usuarioActualizado
                                                                isEditing = false
                                                                Toast.makeText(context, "Alias actualizado correctamente", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                aliasError = "Error al actualizar alias"
                                                                Toast.makeText(context, "Error al actualizar alias", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        aliasError = "Error: ${e.message}"
                                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    } finally {
                                                        isLoading = false
                                                    }
                                                }
                                            } else {
                                                aliasError = "El alias no puede estar vacío"
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        enabled = nuevoAlias.isNotBlank() && nuevoAlias != usuario?.alias && !isLoading
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text("Guardar")
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = usuario?.alias ?: "No disponible",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón de logout
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Cerrar Sesión",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    // Diálogo de confirmación de logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        // Cerrar sesión en Firebase
                        auth.signOut()
                        // Navegar al login y limpiar el stack
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}