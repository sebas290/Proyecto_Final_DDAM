package com.example.clasebeforeproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.clasebeforeproject.Nav.AppNavHost
import com.example.clasebeforeproject.ui.theme.MyAppTheme
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase
        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        // Room
        val roomDb = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "usuarios_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // Repositorios
        val usuariosRepository = UsuariosRepository(roomDb.UserDao())
        val juegosRepository = JuegoRepository(roomDb.GameDao())
        val reseñasRepository = ReviewsRepository(roomDb.ReviewDao(), roomDb.GameDao())

        // ViewModels
        val usuariosViewModel = ViewModelProvider(
            this, UsuariosViewModelFactory(usuariosRepository)
        )[UsuariosViewModel::class.java]

        // CAMBIO AQUÍ: Pasar ambos repositorios al JuegosViewModelFactory
        val juegosViewModel = ViewModelProvider(
            this, JuegosViewModelFactory(juegosRepository, reseñasRepository)
        )[JuegosViewModel::class.java]

        val reseñasViewModel = ViewModelProvider(
            this, ReviewViewModelFactory(reseñasRepository)
        )[ReviewViewModel::class.java]

        setContent {
            val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)

            var themePref by remember { mutableStateOf(prefs.getString("theme", "system") ?: "system") }
            var fontPref by remember { mutableStateOf(prefs.getString("font_size", "medium") ?: "medium") }
            var highContrastPref by remember { mutableStateOf(prefs.getBoolean("accessibility_high_contrast", false)) }

            val isDarkTheme = when (themePref) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            MyAppTheme(
                darkTheme = isDarkTheme,
                highContrast = highContrastPref,
                fontSizePref = fontPref
            ) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    AppNavHost(
                        navController = navController,
                        auth = auth,
                        db = db,
                        usuariosViewModel = usuariosViewModel,
                        juegosViewModel = juegosViewModel,
                        reseñasViewModel = reseñasViewModel,
                        onSettingsChanged = { newTheme, newFont, newHighContrast ->
                            themePref = newTheme
                            fontPref = newFont
                            highContrastPref = newHighContrast

                            // Guardar en SharedPreferences
                            prefs.edit()
                                .putString("theme", newTheme)
                                .putString("font_size", newFont)
                                .putBoolean("accessibility_high_contrast", newHighContrast)
                                .apply()
                        }
                    )
                }
            }
        }
    }
}