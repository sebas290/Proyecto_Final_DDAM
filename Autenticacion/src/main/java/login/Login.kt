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
    var showAlias by remember { mutableStateOf(false) }
    var aliasError by remember { mutableStateOf("") }

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
        TextField(
            value = email,
            onValueChange = {
                email = it
                showAlias = true
            },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") }
        )
        Spacer(Modifier.height(8.dp))

        if (showAlias) {
            TextField(
                value = alias,
                onValueChange = { if (it.length <= 10) alias = it },
                label = { Text("Alias (max 10)") },
                isError = aliasError.isNotEmpty(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = "Alias") }
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
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") }
        )
        Spacer(Modifier.height(16.dp))

        // Login Email
        Button(
            onClick = {
                if (showAlias && alias.isBlank()) {
                    aliasError = "Alias obligatorio"
                    return@Button
                }
                aliasError = ""
                scope.launch {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            scope.launch {
                                val usuario = viewModel.getUsuarioByCorreo(email)
                                usuario?.let { onSuccess(it.id) }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Login, contentDescription = "Email Login")
            Spacer(Modifier.width(8.dp))
            Text("Iniciar sesión con Email")
        }

        Spacer(Modifier.height(8.dp))

        // Login Google
        Button(
            onClick = { launcher.launch(googleSignInClient.signInIntent) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_gmail_1),
                contentDescription = "Google Login",
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Iniciar sesión con Google")
        }

        Spacer(Modifier.height(8.dp))

        // Registro
        Button(
            onClick = {
                if (alias.isBlank()) {
                    aliasError = "Alias obligatorio"
                    return@Button
                }
                aliasError = ""
                scope.launch {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            scope.launch {
                                viewModel.addUsuario(Usuarios(0, alias, email, password))
                                val usuario = viewModel.getUsuarioByCorreo(email)
                                usuario?.let { onSuccess(it.id) }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = "Registrar")
            Spacer(Modifier.width(8.dp))
            Text("Registrarse")
        }
    }
}
