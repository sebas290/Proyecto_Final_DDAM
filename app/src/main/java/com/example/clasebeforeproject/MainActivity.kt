package com.example.clasebeforeproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.clasebeforeproject.Nav.AppNavHost
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
            .fallbackToDestructiveMigration() // borra y recrea si cambia el esquema
            .build()

        // Repositorios
        val usuariosRepository = UsuariosRepository(roomDb.UserDao())
        val juegosRepository = JuegoRepository(roomDb.GameDao())
        val reseñasRepository = ReviewsRepository(roomDb.ReviewDao())

        // ViewModels
        val usuariosViewModel = ViewModelProvider(
            this, UsuariosViewModelFactory(usuariosRepository)
        )[UsuariosViewModel::class.java]

        val juegosViewModel = ViewModelProvider(
            this, JuegosViewModelFactory(juegosRepository)
        )[JuegosViewModel::class.java]

        val reseñasViewModel = ViewModelProvider(
            this, ReviewViewModelFactory(reseñasRepository)
        )[ReviewViewModel::class.java]

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                val navController = rememberNavController()

                AppNavHost(
                    navController = navController,
                    auth = auth,
                    db = db,
                    usuariosViewModel = usuariosViewModel,
                    juegosViewModel = juegosViewModel,
                    reseñasViewModel = reseñasViewModel
                )
            }
        }
    }
}
