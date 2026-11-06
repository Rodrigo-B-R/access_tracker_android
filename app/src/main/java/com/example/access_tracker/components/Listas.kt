package com.example.access_tracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto

@Composable
fun Avatar(
    iniciales: String,
    modifier: Modifier = Modifier,
    fondo: Color = Petroleo100,
    tinta: Color = Petroleo700,
    lado: Dp = 44.dp,
) {
    Box(modifier.size(lado).clip(CircleShape).background(fondo), contentAlignment = Alignment.Center) {
        Text(iniciales, style = if (lado < 30.dp) Texto.LabelS else Texto.LabelM, color = tinta)
    }
}

@Composable
fun FilaPersona(
    iniciales: String,
    nombre: String,
    detalle: String,
    modifier: Modifier = Modifier,
    fondoAvatar: Color = Petroleo100,
    tintaAvatar: Color = Petroleo700,
    ladoAvatar: Dp = 44.dp,
    fondo: Color = FondoBlanco,
    onClick: (() -> Unit)? = null,
    final: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(fondo)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(start = 12.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(iniciales, fondo = fondoAvatar, tinta = tintaAvatar, lado = ladoAvatar)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(nombre, style = Texto.LabelL, color = TintaBase)
            Text(detalle, style = Texto.BodyS, color = TintaSuave)
        }
        final()
    }
}

@Composable
fun Casilla(marcada: Boolean, modifier: Modifier = Modifier, bloqueada: Boolean = false, onClick: (() -> Unit)? = null) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(if (bloqueada) FondoArena else if (marcada) Petroleo700 else Color.Transparent)
            .then(if (!marcada && !bloqueada) Modifier.border(2.dp, TintaTenue, RoundedCornerShape(9.dp)) else Modifier)
            .then(if (onClick != null && !bloqueada) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (bloqueada) Icono(Ico.Check, tint = TintaTenue, tamano = 18.dp)
        else if (marcada) Icono(Ico.Check, tint = FondoCrema, tamano = 18.dp)
    }
}

@Composable
fun Flecha(modifier: Modifier = Modifier, tint: Color = TintaSuave) {
    Icono(Ico.Chevron, tint = tint, tamano = 20.dp, modifier = modifier)
}

@Composable
fun BotonCircular(
    icono: Ico,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fondo: Color = FondoArena,
    lado: Dp = 48.dp,
    tint: Color = TintaBase,
) {
    Box(modifier.size(lado).clip(CircleShape).background(fondo).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icono(icono, tint = tint)
    }
}

@Composable
fun BotonFlotanteIcono(icono: Ico, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(64.dp),
        shape = RoundedCornerShape(22.dp),
        containerColor = color,
        contentColor = TintaBase
    ) {
        Icono(icono, tint = TintaBase, tamano = 26.dp)
    }
}

@Composable
fun BarraBusqueda(texto: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(FondoArena).padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(Ico.Search, tint = TintaSuave, tamano = 20.dp)
        Text(texto, style = Texto.BodyL, color = TintaSuave)
    }
}

@Composable
fun FiltroPill(texto: String, activo: Boolean, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (activo) TintaBase else FondoArena)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
    ) { Text(texto, style = Texto.LabelM, color = if (activo) FondoCrema else TintaBase) }
}

@Composable
fun EncabezadoSeccion(texto: String, modifier: Modifier = Modifier) {
    Eyebrow(texto, modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 2.dp))
}
