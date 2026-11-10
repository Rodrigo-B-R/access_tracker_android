package com.example.access_tracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.Texto

enum class EstiloBoton(val fondo: Color, val texto: Color) {
    Primario(Mostaza500, TintaBase),
    Oscuro(Petroleo700, FondoCrema),
    Secundario(FondoArena, TintaBase),
    Peligro(Rojo600, FondoBlanco),
    Claro(FondoBlanco, TintaBase),
}

@Composable
fun Boton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    estilo: EstiloBoton = EstiloBoton.Primario,
    icono: Ico? = null,
    habilitado: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .alpha(if (habilitado) 1f else 0.5f)
            .clip(RoundedCornerShape(30.dp))
            .background(estilo.fondo)
            .clickable(enabled = habilitado, onClick = onClick)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icono != null) Icono(icono, tint = estilo.texto, tamano = 22.dp)
        Text(text = texto, style = Texto.TitleS, color = estilo.texto)
    }
}
