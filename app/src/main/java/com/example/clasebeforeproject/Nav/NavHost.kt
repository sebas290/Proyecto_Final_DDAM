package com.example.clasebeforeproject.Nav

import Chat.ChatScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.catalogo.juegos.ListaJuegosScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import login.AuthScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    NavHost(navController = navController, startDestination = "login") {

        // Pantalla de catálogo
        composable("listaJuegos") {
            ListaJuegosScreen(
                navController = navController,
                onChatClick = {
                    navController.navigate("chat")
                }
            )
        }

        // Pantalla de login
        composable("login") {
            AuthScreen(
                auth = auth,
                onSuccess = {
                    // Navega primero a la lista de juegos
                    navController.navigate("listaJuegos") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de chat
        composable("chat") {
            val user = auth.currentUser?.email ?: "anonymous"
            ChatScreen(db = db, user = user)
        }
    }
}
