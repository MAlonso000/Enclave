package com.marioalonso.enclave.pruebas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.ui.theme.EnclaveTheme // Importa tu tema personalizado

@Composable
fun ColorSchemePreview() {
    val colorScheme = MaterialTheme.colorScheme

    // Lista de pares: Nombre del color y el Color en sí
    val colors = listOf(
        "primary" to colorScheme.primary,
        "onPrimary" to colorScheme.onPrimary,
        "primaryContainer" to colorScheme.primaryContainer,
        "onPrimaryContainer" to colorScheme.onPrimaryContainer,
        "inversePrimary" to colorScheme.inversePrimary,
        "secondary" to colorScheme.secondary,
        "onSecondary" to colorScheme.onSecondary,
        "secondaryContainer" to colorScheme.secondaryContainer,
        "onSecondaryContainer" to colorScheme.onSecondaryContainer,
        "tertiary" to colorScheme.tertiary,
        "onTertiary" to colorScheme.onTertiary,
        "tertiaryContainer" to colorScheme.tertiaryContainer,
        "onTertiaryContainer" to colorScheme.onTertiaryContainer,
        "background" to colorScheme.background,
        "onBackground" to colorScheme.onBackground,
        "surface" to colorScheme.surface,
        "onSurface" to colorScheme.onSurface,
        "surfaceVariant" to colorScheme.surfaceVariant,
        "onSurfaceVariant" to colorScheme.onSurfaceVariant,
        "surfaceTint" to colorScheme.surfaceTint,
        "inverseSurface" to colorScheme.inverseSurface,
        "inverseOnSurface" to colorScheme.inverseOnSurface,
        "error" to colorScheme.error,
        "onError" to colorScheme.onError,
        "errorContainer" to colorScheme.errorContainer,
        "onErrorContainer" to colorScheme.onErrorContainer,
        "outline" to colorScheme.outline,
        "outlineVariant" to colorScheme.outlineVariant,
        "scrim" to colorScheme.scrim,
        // Añade aquí cualquier otro color personalizado que hayas definido en tu ColorScheme
        // "customColor" to colorScheme.customColor (si existe)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Para poder hacer scroll si hay muchos colores
            .padding(16.dp)
    ) {
        colors.forEach { (name, color) ->
            ColorChip(name = name, color = color)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ColorChip(name: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color = color, shape = MaterialTheme.shapes.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(start = 16.dp),
            // Elige un color de texto que contraste bien con el color de fondo del chip
            // Aquí usamos onPrimary para el color primario, onSecondary para el secundario, etc.
            // Para un caso general, podrías necesitar una lógica para elegir blanco o negro.
            color = when (name) {
                "primary" -> MaterialTheme.colorScheme.onPrimary
                "secondary" -> MaterialTheme.colorScheme.onSecondary
                "tertiary" -> MaterialTheme.colorScheme.onTertiary
                "error" -> MaterialTheme.colorScheme.onError
                "background" -> MaterialTheme.colorScheme.onBackground
                "surface" -> MaterialTheme.colorScheme.onSurface
                // ... y así sucesivamente para los colores "on<Color>"
                // Para una solución más robusta para el color del texto, ver más abajo.
                else -> LocalContentColor.current // O un color de contraste calculado
            }
        )
        Text(
            text = colorToHexString(color),
            modifier = Modifier.padding(end = 16.dp),
            color = when (name) {
                "primary" -> MaterialTheme.colorScheme.onPrimary
                "secondary" -> MaterialTheme.colorScheme.onSecondary
                "tertiary" -> MaterialTheme.colorScheme.onTertiary
                "error" -> MaterialTheme.colorScheme.onError
                "background" -> MaterialTheme.colorScheme.onBackground
                "surface" -> MaterialTheme.colorScheme.onSurface
                else -> LocalContentColor.current
            }
        )
    }
}

fun colorToHexString(color: Color): String {
    return String.format(
        "#%02X%02X%02X%02X",
        (color.alpha * 255).toInt(),
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt()
    )
}

@Preview(showBackground = true, name = "Light Theme Colors")
@Composable
fun PreviewColorSchemeLight() {
    EnclaveTheme(darkTheme = false) { // Asegúrate de usar tu tema
        ColorSchemePreview()
    }
}

@Preview(showBackground = true, name = "Dark Theme Colors")
@Composable
fun PreviewColorSchemeDark() {
    EnclaveTheme(darkTheme = true) { // Asegúrate de usar tu tema
        ColorSchemePreview()
    }
}