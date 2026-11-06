package com.example.access_tracker.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Petroleo500
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.Texto

@Composable
fun MenuCuenta(
    onCambiarPassword: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    oscuro: Boolean = false,
) {
    var abierto by remember { mutableStateOf(false) }
    Box(modifier) {
        BotonCircular(
            Ico.More, { abierto = true },
            fondo = if (oscuro) Petroleo500 else FondoArena,
            tint = if (oscuro) FondoCrema else TintaBase,
            lado = 44.dp,
        )
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }, containerColor = FondoBlanco) {
            DropdownMenuItem(
                text = { Text("Cambiar contraseña", style = Texto.BodyL, color = TintaBase) },
                leadingIcon = { Icono(Ico.Lock, tint = TintaBase, tamano = 20.dp) },
                onClick = { abierto = false; onCambiarPassword() },
            )
            DropdownMenuItem(
                text = { Text("Cerrar sesión", style = Texto.BodyL, color = Rojo600) },
                leadingIcon = { Icono(Ico.Logout, tint = Rojo600, tamano = 20.dp) },
                onClick = { abierto = false; onCerrarSesion() },
            )
        }
    }
}
