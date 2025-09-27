package com.example.clasebeforeproject.Nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.catalogo.juegos.AgregarJuegoScreen
import com.example.catalogo.juegos.ListaJuegosScreen
import com.example.catalogo.juegos.ListaReseñasScreen
import com.example.catalogo.reseñas.AgregarReseñaScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import login.AuthScreen
import login.PerfilScreen
import com.example.data.model.UsuariosViewModel
import com.example.data.model.JuegosViewModel
import com.example.data.model.ReviewViewModel
import Settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    usuariosViewModel: UsuariosViewModel,
    juegosViewModel: JuegosViewModel,
    reseñasViewModel: ReviewViewModel,
    onSettingsChanged: (String, String, Boolean) -> Unit
) {
    NavHost(navController = navController, startDestination = "login") {

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

        composable("listaJuegos/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0
            ListaJuegosScreen(
                navController = navController,
                viewModel = juegosViewModel,
                onChatClick = { juegoId ->
                    navController.navigate("listaResenas/$juegoId")
                },
                usuarioId = usuarioId
            )
        }

        // Ruta unificada para agregar/editar juego
        composable("agregarJuego/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0
            AgregarJuegoScreen(
                navController = navController,
                juegosViewModel = juegosViewModel,
                usuarioId = usuarioId,
                juegoId = null // null = modo agregar
            )
        }

        // Nueva ruta para editar juego (reutiliza la misma pantalla)
        composable("agregarJuego/{usuarioId}/{juegoId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0
            val juegoId = backStackEntry.arguments?.getString("juegoId")?.toIntOrNull()
            AgregarJuegoScreen(
                navController = navController,
                juegosViewModel = juegosViewModel,
                usuarioId = usuarioId,
                juegoId = juegoId // valor = modo editar
            )
        }

        composable("listaResenas/{juegoId}") { backStackEntry ->
            val juegoId = backStackEntry.arguments?.getString("juegoId")?.toIntOrNull() ?: 0
            ListaReseñasScreen(
                navController = navController,
                reviewViewModel = reseñasViewModel,
                juegoId = juegoId
            )
        }

        composable("agregarReseña/{juegoId}/{usuarioId}") { backStackEntry ->
            val juegoId = backStackEntry.arguments?.getString("juegoId")?.toIntOrNull() ?: 0
            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0
            AgregarReseñaScreen(
                navController = navController,
                reviewViewModel = reseñasViewModel,
                juegoId = juegoId,
                usuarioId = usuarioId
            )
        }

        composable("settings") {
            SettingsScreen(
                navController = navController,
                onSettingsChanged = onSettingsChanged
            )
        }

        // Nueva ruta para la pantalla de perfil
        composable("perfil/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0
            PerfilScreen(
                navController = navController,
                usuariosViewModel = usuariosViewModel,
                auth = auth,
                usuarioId = usuarioId
            )
        }
    }
}