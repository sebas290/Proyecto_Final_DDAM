package com.example.clasebeforeproject.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    // Colores claros
)

private val DarkColors = darkColorScheme(
    // Colores oscuros
)

private val HighContrastLightColors = lightColorScheme(
    primary = LightColors.primary.copy(alpha = 1f),
    // Ajusta los demás colores para alto contraste
)

private val HighContrastDarkColors = darkColorScheme(
    primary = DarkColors.primary.copy(alpha = 1f),
    // Ajusta los demás colores para alto contraste
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
        bodySmall = TextStyle(fontSize = size)
    )
}

@Composable
fun MyAppTheme(
    darkTheme: Boolean,
    highContrast: Boolean,
    fontSizePref: String,
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
