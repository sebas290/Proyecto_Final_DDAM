package Chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects

data class Message(val user: String = "", val text: String = "")

@Composable
fun ChatScreen(db: FirebaseFirestore, user: String) {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var input by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        db.collection("chat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) messages = snapshot.toObjects()
            }
    }

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                // Corregido: Usa el composable Text de Material3
                Text(text = "${msg.user}: ${msg.text}")
            }
        }

        Row {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                val newMsg = hashMapOf(
                    "user" to user,
                    "text" to input,
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("chat").add(newMsg)
                input = ""
            }) {
                Text("Enviar")
            }
        }
    }
}