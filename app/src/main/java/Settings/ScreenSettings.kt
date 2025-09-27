package Settings

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onSettingsChanged: (String, String, Boolean) -> Unit
) {
    val PREFS = "app_settings"
    val KEY_THEME = "theme"
    val KEY_FONT = "font_size"
    val KEY_ACCESS = "accessibility_high_contrast"

    val ctx = LocalContext.current
    val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    var theme by remember { mutableStateOf(prefs.getString(KEY_THEME, "system") ?: "system") }
    var font by remember { mutableStateOf(prefs.getString(KEY_FONT, "medium") ?: "medium") }
    var highContrast by remember { mutableStateOf(prefs.getBoolean(KEY_ACCESS, false)) }

    fun save() {
        prefs.edit()
            .putString(KEY_THEME, theme)
            .putString(KEY_FONT, font)
            .putBoolean(KEY_ACCESS, highContrast)
            .apply()

        onSettingsChanged(theme, font, highContrast)  // Actualiza estado en MainActivity
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            // Tema
            Text("Tema", style = MaterialTheme.typography.titleMedium)
            Row {
                listOf("system", "light", "dark").forEach { t ->
                    FilterChip(
                        selected = theme == t,
                        onClick = {
                            theme = t
                            save()
                        },
                        label = { Text(t.replaceFirstChar { it.uppercaseChar() }) },
                        leadingIcon = if (theme == t) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tamaño de letra
            Text("Tamaño de letra", style = MaterialTheme.typography.titleMedium)
            Row {
                listOf("small", "medium", "large").forEach { f ->
                    FilterChip(
                        selected = font == f,
                        onClick = {
                            font = f
                            save()
                        },
                        label = { Text(f.replaceFirstChar { it.uppercaseChar() }) },
                        leadingIcon = if (font == f) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Accesibilidad
            Text("Accesibilidad", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = highContrast,
                    onCheckedChange = {
                        highContrast = it
                        save()
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text("Alto contraste / fuentes legibles")
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    save()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}