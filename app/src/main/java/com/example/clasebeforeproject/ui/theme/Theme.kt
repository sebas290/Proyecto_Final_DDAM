package com.example.clasebeforeproject.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Cyan40,                    // Cyan brillante para elementos principales
    onPrimary = CyberGrey40,            // Texto sobre el color primario
    primaryContainer = OrangeGrey80,     // Contenedor primario
    onPrimaryContainer = CyberGrey40,    // Texto sobre contenedor primario

    secondary = Orange80,                // Naranja para elementos secundarios
    onSecondary = CyberGrey40,          // Texto sobre secundario
    secondaryContainer = OrangeGrey80,   // Contenedor secundario
    onSecondaryContainer = CyberGrey40,  // Texto sobre contenedor secundario

    tertiary = Purple40,                 // Púrpura para elementos terciarios
    onTertiary = Color.White,           // Texto sobre terciario
    tertiaryContainer = OrangeGrey80,    // Contenedor terciario
    onTertiaryContainer = CyberGrey40,   // Texto sobre contenedor terciario

    background = Color.White,            // Fondo principal
    onBackground = CyberGrey40,         // Texto sobre fondo
    surface = Color.White,              // Superficie
    onSurface = CyberGrey40,            // Texto sobre superficie
    surfaceVariant = OrangeGrey80,      // Variante de superficie
    onSurfaceVariant = CyberGrey40,     // Texto sobre variante de superficie

    outline = Purple40,                  // Contornos
    outlineVariant = Orange80,          // Variante de contornos

    error = Color(0xFFD32F2F),          // Color de error (rojo estándar)
    onError = Color.White,              // Texto sobre error
    errorContainer = Color(0xFFFFEBEE), // Contenedor de error
    onErrorContainer = Color(0xFFB71C1C) // Texto sobre contenedor de error
)

private val DarkColors = darkColorScheme(
    primary = Cyan40,                    // Cyan brillante mantiene su intensidad en oscuro
    onPrimary = CyberGrey40,            // Texto sobre primario
    primaryContainer = CyberGrey40,      // Contenedor primario oscuro
    onPrimaryContainer = Cyan40,         // Texto sobre contenedor primario

    secondary = Peach80,                 // Durazno claro para contraste en oscuro
    onSecondary = CyberGrey40,          // Texto sobre secundario
    secondaryContainer = CyberGrey40,    // Contenedor secundario oscuro
    onSecondaryContainer = Peach80,      // Texto sobre contenedor secundario

    tertiary = Purple40,                 // Púrpura mantiene su presencia
    onTertiary = Color.White,           // Texto sobre terciario
    tertiaryContainer = CyberGrey40,     // Contenedor terciario oscuro
    onTertiaryContainer = Purple40,      // Texto sobre contenedor terciario

    background = CyberGrey40,            // Fondo principal oscuro
    onBackground = Color.White,          // Texto sobre fondo oscuro
    surface = CyberGrey40,              // Superficie oscura
    onSurface = Color.White,            // Texto sobre superficie oscura
    surfaceVariant = Color(0xFF2A2435), // Variante de superficie (cyber grey más claro)
    onSurfaceVariant = OrangeGrey80,    // Texto sobre variante de superficie

    outline = Orange80,                  // Contornos naranjas en oscuro
    outlineVariant = Peach80,           // Variante de contornos

    error = Color(0xFFEF5350),          // Color de error más claro para oscuro
    onError = CyberGrey40,              // Texto sobre error
    errorContainer = Color(0xFF8E2633), // Contenedor de error oscuro
    onErrorContainer = Color(0xFFFFDAD6) // Texto sobre contenedor de error
)

private val HighContrastLightColors = lightColorScheme(
    primary = Cyan40,                    // Cyan con máximo contraste
    onPrimary = Color.Black,            // Negro puro para máximo contraste
    primaryContainer = Color.White,      // Blanco puro
    onPrimaryContainer = Color.Black,    // Negro puro

    secondary = Orange80,                // Naranja intenso
    onSecondary = Color.Black,          // Negro puro
    secondaryContainer = Color.White,    // Blanco puro
    onSecondaryContainer = Color.Black,  // Negro puro

    tertiary = Purple40,                 // Púrpura intenso
    onTertiary = Color.White,           // Blanco puro
    tertiaryContainer = Color.White,     // Blanco puro
    onTertiaryContainer = Color.Black,   // Negro puro

    background = Color.White,            // Blanco puro
    onBackground = Color.Black,          // Negro puro
    surface = Color.White,              // Blanco puro
    onSurface = Color.Black,            // Negro puro
    surfaceVariant = Color.White,       // Blanco puro
    onSurfaceVariant = Color.Black,     // Negro puro

    outline = Color.Black,              // Contornos negros
    outlineVariant = CyberGrey40,       // Variante cyber grey

    error = Color(0xFFB71C1C),          // Rojo intenso
    onError = Color.White,              // Blanco puro
    errorContainer = Color.White,        // Blanco puro
    onErrorContainer = Color(0xFFB71C1C) // Rojo intenso
)

private val HighContrastDarkColors = darkColorScheme(
    primary = Cyan40,                    // Cyan brillante
    onPrimary = Color.Black,            // Negro para contraste
    primaryContainer = Color.Black,      // Negro puro
    onPrimaryContainer = Cyan40,         // Cyan brillante

    secondary = Peach80,                 // Durazno claro intenso
    onSecondary = Color.Black,          // Negro puro
    secondaryContainer = Color.Black,    // Negro puro
    onSecondaryContainer = Peach80,      // Durazno claro

    tertiary = Purple40,                 // Púrpura intenso
    onTertiary = Color.Black,           // Negro para contraste
    tertiaryContainer = Color.Black,     // Negro puro
    onTertiaryContainer = Purple40,      // Púrpura intenso

    background = Color.Black,            // Negro puro
    onBackground = Color.White,          // Blanco puro
    surface = Color.Black,              // Negro puro
    onSurface = Color.White,            // Blanco puro
    surfaceVariant = Color.Black,       // Negro puro
    onSurfaceVariant = Color.White,     // Blanco puro

    outline = Color.White,              // Contornos blancos
    outlineVariant = Orange80,          // Variante naranja

    error = Color(0xFFFF5252),          // Rojo brillante
    onError = Color.Black,              // Negro puro
    errorContainer = Color.Black,        // Negro puro
    onErrorContainer = Color(0xFFFF5252) // Rojo brillante
)

fun typographyWithFontSize(fontSize: String): Typography {
    val size = when (fontSize) {
        "small" -> 12.sp
        "large" -> 20.sp
        else -> 16.sp
    }
    return Typography(
        bodyLarge = TextStyle(fontSize = size),
        bodyMedium = TextStyle(fontSize = size),
        bodySmall = TextStyle(fontSize = size),
        headlineLarge = TextStyle(fontSize = (size.value * 2).sp),
        headlineMedium = TextStyle(fontSize = (size.value * 1.5f).sp),
        headlineSmall = TextStyle(fontSize = (size.value * 1.25f).sp),
        titleLarge = TextStyle(fontSize = (size.value * 1.4f).sp),
        titleMedium = TextStyle(fontSize = (size.value * 1.2f).sp),
        titleSmall = TextStyle(fontSize = (size.value * 1.1f).sp),
        labelLarge = TextStyle(fontSize = (size.value * 0.9f).sp),
        labelMedium = TextStyle(fontSize = (size.value * 0.8f).sp),
        labelSmall = TextStyle(fontSize = (size.value * 0.7f).sp)
    )
}

@Composable
fun MyAppTheme(
    darkTheme: Boolean = false,
    highContrast: Boolean = false,
    fontSizePref: String = "normal",
    content: @Composable () -> Unit
) {
    val colors = when {
        darkTheme && highContrast -> HighContrastDarkColors
        darkTheme -> DarkColors
        !darkTheme && highContrast -> HighContrastLightColors
        else -> LightColors
    }

    val typography = typographyWithFontSize(fontSizePref)

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content
    )
}