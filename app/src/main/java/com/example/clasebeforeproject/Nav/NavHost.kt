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
import com.example.data.model.WorkViewModel
import Settings.SettingsScreen
import com.example.clasebeforeproject.Splash.SplashScreen
import com.example.clasebeforeproject.screens.WorkManagerScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    usuariosViewModel: UsuariosViewModel,
    juegosViewModel: JuegosViewModel,
    reseñasViewModel: ReviewViewModel,
    workViewModel: WorkViewModel,
    onSettingsChanged: (String, String, Boolean) -> Unit
) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController = navController)
        }

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
                    // 🎯 CORREGIDO: Pasar usuarioId a la navegación
                    navController.navigate("listaResenas/$juegoId/$usuarioId")
                },
                usuarioId = usuarioId
            )
        }

        composable("agregarJuego/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0
            AgregarJuegoScreen(
                navController = navController,
                juegosViewModel = juegosViewModel,
                usuarioId = usuarioId,
                juegoId = null
            )
        }

        composable("agregarJuego/{usuarioId}/{juegoId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0
            val juegoId = backStackEntry.arguments?.getString("juegoId")?.toIntOrNull()
            AgregarJuegoScreen(
                navController = navController,
                juegosViewModel = juegosViewModel,
                usuarioId = usuarioId,
                juegoId = juegoId
            )
        }

        // 🎯 CORREGIDO: Ruta actualizada para incluir usuarioId
        composable("listaResenas/{juegoId}/{usuarioId}") { backStackEntry ->
            val juegoId = backStackEntry.arguments?.getString("juegoId")?.toIntOrNull() ?: 0
            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0
            ListaReseñasScreen(
                navController = navController,
                reviewViewModel = reseñasViewModel,
                juegoId = juegoId,
                usuarioId = usuarioId
            )
        }

        composable("agregarReseña/{juegoId}/{usuarioId}") { backStackEntry ->
            val juegoId = backStackEntry.arguments?.getString("juegoId")?.toIntOrNull() ?: 0
            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: 0
            AgregarReseñaScreen(
                navController = navController,
                reviewViewModel = reseñasViewModel,
                juegosViewModel = juegosViewModel,
                juegoId = juegoId,
                usuarioId = usuarioId
            )
        }

        composable("workmanager") {
            WorkManagerScreen(
                workViewModel = workViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                navController = navController,
                onSettingsChanged = onSettingsChanged
            )
        }

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