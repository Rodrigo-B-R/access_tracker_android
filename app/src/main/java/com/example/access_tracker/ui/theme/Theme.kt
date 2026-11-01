package com.example.access_tracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// El diseño solo define tema claro y no usa color dinámico: la paleta es de la marca.
private val EsquemaClaro = lightColorScheme(
    primary = Petroleo700,
    onPrimary = FondoCrema,
    secondary = Mostaza500,
    onSecondary = TintaBase,
    tertiary = Coral500,
    onTertiary = TintaBase,
    background = FondoCrema,
    onBackground = TintaBase,
    surface = FondoCrema,
    onSurface = TintaBase,
    surfaceVariant = FondoArena,
    onSurfaceVariant = TintaSuave,
    outline = TintaTenue,
    error = Rojo600,
    onError = FondoBlanco,
)

@Composable
fun Access_trackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Typography,
        content = content
    )
}
