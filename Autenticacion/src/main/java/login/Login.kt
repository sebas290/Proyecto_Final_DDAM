package login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.model.UsuariosViewModel
import com.example.data.database.Usuarios
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.example.autenticacion.R

@Composable
fun AuthScreen(
    auth: FirebaseAuth,
    onSuccess: (Int) -> Unit,
    viewModel: UsuariosViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) } // Nuevo: separar login de registro
    var aliasError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Para mostrar estado de carga

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    // Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("955903094104-b0hfdtapvq95l4l8ukisk17h1nhmnkv0.apps.googleusercontent.com")
        .requestEmail()
        .build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            val correo = account?.email
            val displayName = account?.displayName ?: correo?.substringBefore("@") ?: "user"

            if (idToken != null && correo != null) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show()
                            scope.launch {
                                var existente = viewModel.getUsuarioByCorreo(correo)
                                if (existente == null) {
                                    // NO guardamos contraseña para usuarios de Google
                                    viewModel.addUsuario(Usuarios(0, displayName, correo, ""))
                                    existente = viewModel.getUsuarioByCorreo(correo)
                                }
                                existente?.let { onSuccess(it.id) }

                                // Guardar en Firestore
                                val uid = auth.currentUser?.uid
                                if (uid != null) {
                                    firestore.collection("usuarios").document(uid)
                                        .set(
                                            mapOf(
                                                "alias" to displayName,
                                                "correo" to correo,
                                                "provider" to "google",
                                                "createdAt" to com.google.firebase.Timestamp.now()
                                            )
                                        )
                                }
                            }
                        } else {
                            Toast.makeText(context, "Error Firebase: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Sign-In falló: ${e.statusCode}", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.Center
    ) {
        // Toggle entre Login y Registro
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = { isLoginMode = true }) {
                Text(
                    text = "Iniciar Sesión",
                    color = if (isLoginMode) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
            Text(" | ")
            TextButton(onClick = { isLoginMode = false }) {
                Text(
                    text = "Registrarse",
                    color = if (!isLoginMode) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            enabled = !isLoading
        )
        Spacer(Modifier.height(8.dp))

        // Mostrar alias solo en modo registro
        if (!isLoginMode) {
            TextField(
                value = alias,
                onValueChange = { if (it.length <= 10) alias = it },
                label = { Text("Alias (max 10)") },
                isError = aliasError.isNotEmpty(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = "Alias") },
                enabled = !isLoading
            )
            if (aliasError.isNotEmpty()) {
                Text(aliasError, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))
        }

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            enabled = !isLoading
        )
        Spacer(Modifier.height(16.dp))

        if (isLoginMode) {
            // Botón de LOGIN
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Email y contraseña son obligatorios", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        // Primero verificar si el usuario existe en la base de datos local
                        val usuarioLocal = viewModel.getUsuarioByCorreo(email)
                        if (usuarioLocal == null) {
                            isLoading = false
                            Toast.makeText(context, "Usuario no registrado. Regístrate primero.", Toast.LENGTH_LONG).show()
                            return@launch
                        }

                        // Intentar login con Firebase
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Login exitoso", Toast.LENGTH_SHORT).show()
                                scope.launch {
                                    val usuario = viewModel.getUsuarioByCorreo(email)
                                    usuario?.let { onSuccess(it.id) }
                                }
                            }
                            .addOnFailureListener { exception ->
                                isLoading = false
                                val errorMsg = when {
                                    exception.message?.contains("password is invalid") == true ->
                                        "Contraseña incorrecta"
                                    exception.message?.contains("user-not-found") == true ->
                                        "Usuario no encontrado"
                                    exception.message?.contains("invalid-email") == true ->
                                        "Email inválido"
                                    else -> "Error de autenticación: ${exception.message}"
                                }
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Icon(Icons.Default.Login, contentDescription = "Email Login")
                }
                Spacer(Modifier.width(8.dp))
                Text(if (isLoading) "Iniciando..." else "Iniciar Sesión")
            }
        } else {
            // Botón de REGISTRO
            Button(
                onClick = {
                    if (alias.isBlank()) {
                        aliasError = "Alias obligatorio"
                        return@Button
                    }
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    aliasError = ""
                    isLoading = true

                    scope.launch {
                        // Verificar si ya existe el usuario
                        val usuarioExistente = viewModel.getUsuarioByCorreo(email)
                        if (usuarioExistente != null) {
                            isLoading = false
                            Toast.makeText(context, "Usuario ya existe. Inicia sesión.", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                scope.launch {
                                    // NO guardamos la contraseña en la base de datos local
                                    // Firebase Auth maneja las contraseñas
                                    viewModel.addUsuario(Usuarios(0, alias, email, ""))

                                    // Guardar en Firestore también
                                    val uid = auth.currentUser?.uid
                                    if (uid != null) {
                                        firestore.collection("usuarios").document(uid)
                                            .set(
                                                mapOf(
                                                    "alias" to alias,
                                                    "correo" to email,
                                                    "provider" to "email",
                                                    "createdAt" to com.google.firebase.Timestamp.now()
                                                )
                                            )
                                    }

                                    isLoading = false
                                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    val usuario = viewModel.getUsuarioByCorreo(email)
                                    usuario?.let { onSuccess(it.id) }
                                }
                            }
                            .addOnFailureListener { exception ->
                                isLoading = false
                                val errorMsg = when {
                                    exception.message?.contains("email-already-in-use") == true ->
                                        "Email ya está en uso"
                                    exception.message?.contains("weak-password") == true ->
                                        "Contraseña muy débil (mínimo 6 caracteres)"
                                    exception.message?.contains("invalid-email") == true ->
                                        "Email inválido"
                                    else -> "Error de registro: ${exception.message}"
                                }
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Registrar")
                }
                Spacer(Modifier.width(8.dp))
                Text(if (isLoading) "Registrando..." else "Registrarse")
            }
        }

        Spacer(Modifier.height(8.dp))

        // Divisor
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("── O ──", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(8.dp))

        // Login Google (siempre disponible)
        Button(
            onClick = { launcher.launch(googleSignInClient.signInIntent) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_gmail_1),
                contentDescription = "Google Login",
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Continuar con Google")
        }
    }
}