package login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthScreen(auth: FirebaseAuth, onSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener {
                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Iniciar sesión con Email")
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            // TODO: Google Sign-In
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Iniciar sesión con Google")
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            // TODO: Xbox / PSN SDK
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Iniciar sesión con Xbox/PSN")
        }

        Spacer(Modifier.height(8.dp))

        // 🔹 Botón de Registro
        Button(onClick = {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(context, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Registrarse")
        }
    }
}
