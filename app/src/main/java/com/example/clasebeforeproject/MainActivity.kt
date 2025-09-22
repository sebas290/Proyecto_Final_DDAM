package com.example.clasebeforeproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.example.clasebeforeproject.Nav.AppNavHost
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                val navController = rememberNavController()

                AppNavHost(navController = navController, auth = auth, db = db)
            }
        }
    }
}
