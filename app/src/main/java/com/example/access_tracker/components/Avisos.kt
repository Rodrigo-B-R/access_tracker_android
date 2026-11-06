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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde600

@Composable
fun AvisoUrgente(
    titulo: String,
    hace: String,
    modifier: Modifier = Modifier,
    texto: String? = null,
    onClick: (() -> Unit)? = null,
    accion: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Rojo600)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            EtiquetaUrgente()
            Text(hace, style = Texto.BodyS, color = FondoBlanco)
        }
        Text(titulo, style = Texto.TitleM, color = FondoBlanco)
        if (texto != null) Text(texto, style = Texto.BodyM, color = FondoBlanco)
        accion()
    }
}

@Composable
fun EtiquetaUrgente(modifier: Modifier = Modifier, relleno: Int = 10) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(FondoBlanco).padding(horizontal = relleno.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(Ico.Megaphone, tint = Rojo600, tamano = 16.dp)
        Text("Urgente", style = Texto.LabelS, color = Rojo600)
    }
}

@Composable
fun AccionAviso(texto: String, icono: Ico, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(20.dp)).background(FondoBlanco).clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(icono, tint = TintaBase, tamano = 16.dp)
        Text(texto, style = Texto.LabelM, color = TintaBase)
    }
}

@Composable
fun AvisoItem(
    etiqueta: String,
    tono: TonoChip,
    fecha: String,
    titulo: String,
    modifier: Modifier = Modifier,
    sinLeer: Boolean = false,
    fondo: Color? = null,
    onClick: (() -> Unit)? = null,
    pie: @Composable () -> Unit,
) {
    val forma = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(forma)
            .background(fondo ?: if (sinLeer) FondoBlanco else FondoArena)
            .border(2.dp, if (sinLeer) Coral500 else Color.Transparent, forma)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Chip(etiqueta, tono = tono)
            Text(fecha, style = Texto.BodyS, color = TintaTenue)
        }
        Text(titulo, style = Texto.TitleS, color = TintaBase)
        pie()
    }
}

@Composable
fun LecturaConfirmada(modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icono(Ico.Check, tint = Verde600, tamano = 16.dp)
        Text("Lectura confirmada", style = Texto.BodyS, color = Verde600)
    }
}

@Composable
fun SinLeer(modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(Coral500))
        Text("Sin leer", style = Texto.LabelM, color = TintaBase)
    }
}
