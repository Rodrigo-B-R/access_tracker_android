package com.example.access_tracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600

enum class TonoChip(val fondo: Color, val texto: Color, val punto: Color) {
    Neutro(FondoArena, TintaSuave, TintaTenue),
    Exito(Verde100, Verde600, Verde600),
    Alerta(Mostaza100, TintaBase, Mostaza500),
    Peligro(Rojo100, Rojo600, Rojo600),
    Info(Petroleo100, Petroleo700, Petroleo700),
    Coral(Coral100, TintaBase, Coral500),
    Solido(TintaBase, FondoCrema, FondoCrema),
}

@Composable
fun Chip(
    etiqueta: String,
    modifier: Modifier = Modifier,
    tono: TonoChip = TonoChip.Neutro,
    punto: Boolean = true,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(tono.fondo)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (punto) Box(Modifier.size(6.dp).clip(CircleShape).background(tono.punto))
        Text(text = etiqueta, style = Texto.LabelS, color = tono.texto)
    }
}
