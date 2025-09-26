package com.example.clasebeforeproject.Nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.catalogo.juegos.AgregarJuegoScreen
import com.example.catalogo.juegos.ListaJuegosScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import login.AuthScreen
import com.example.data.model.UsuariosViewModel
import com.example.data.model.JuegosViewModel
import com.example.data.model.ReviewViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    usuariosViewModel: UsuariosViewModel,
    juegosViewModel: JuegosViewModel,
    reseñasViewModel: ReviewViewModel
) {
    NavHost(navController = navController, startDestination = "login") {

        // Pantalla de login
        composable("login") {
            AuthScreen(
                auth = auth,
                onSuccess = { usuarioId ->
                    navController.navigate("listaJuegos/$usuarioId") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                viewModel = usuariosViewModel
            )
        }

        // Lista de juegos
        composable("listaJuegos/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toInt() ?: 0
            ListaJuegosScreen(
                navController = navController,
                viewModel = juegosViewModel,
                onChatClick = { /* navegar a reseñas */ },
                usuarioId = usuarioId
            )
        }

        // Pantalla para agregar juego
        composable("agregarJuego/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toInt() ?: 0
            AgregarJuegoScreen(
                navController = navController,
                juegosViewModel = juegosViewModel,
                usuarioId = usuarioId
            )
        }
    }
}
