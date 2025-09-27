package com.example.clasebeforeproject.Splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.autenticacion.R

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0f) }
    val darkTheme = isSystemInDarkTheme() // Detecta modo oscuro

    LaunchedEffect(true) {
        // Animación de zoom
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(1500) // tiempo extra mostrando logo
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = if (darkTheme) com.example.ClaseBeforeProject.R.drawable.splashdarkmode else com.example.ClaseBeforeProject.R.drawable.splashlightmode
            ),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(200.dp)
                .scale(scale.value)
        )
    }
}