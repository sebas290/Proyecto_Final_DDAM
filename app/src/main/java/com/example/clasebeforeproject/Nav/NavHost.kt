package com.example.clasebeforeproject.Nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.autenticacion.AuthScreen
import com.example.catalogo.juegos.ListaJuegosScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import comunicacion.ChatScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    NavHost(navController = navController, startDestination = "login") {

        composable("listaJuegos") {
            ListaJuegosScreen(navController)
        }
        composable("login") {
            AuthScreen(
                auth = auth,
                onSuccess = {
                    navController.navigate("chat") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("chat") {
            val user = auth.currentUser?.email ?: "anonymous"
            ChatScreen(db = db, user = user)
        }
    }
}