package com.example.access_tracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600

enum class EstadoBanner(val fondo: Color, val texto: Color, val icono: Ico, val tintIcono: Color) {
    SinConexion(TintaBase, FondoCrema, Ico.Wifioff, Mostaza500),
    Reconectado(Mostaza100, TintaBase, Ico.Refresh, TintaBase),
    Sincronizado(Verde100, Verde600, Ico.Cloud, Verde600),
}

@Composable
fun BannerConexion(estado: EstadoBanner, texto: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(estado.fondo)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(estado.icono, tint = estado.tintIcono, tamano = 22.dp)
        Text(texto, style = Texto.BodyMMedium, color = estado.texto, modifier = Modifier.weight(1f))
    }
}
