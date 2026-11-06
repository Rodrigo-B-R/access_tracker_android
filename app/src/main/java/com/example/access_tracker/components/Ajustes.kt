package com.example.access_tracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoArenaFuerte
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto

@Composable
fun GrupoFilas(modifier: Modifier = Modifier, relleno: androidx.compose.ui.unit.Dp = 16.dp, contenido: @Composable () -> Unit) {
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(FondoBlanco).padding(horizontal = relleno, vertical = 4.dp),
    ) { contenido() }
}

@Composable
fun Divisor(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(1.dp).background(FondoArenaFuerte))
}

@Composable
fun FilaAjuste(
    icono: Ico,
    fondoIcono: Color,
    titulo: String,
    descripcion: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconoCuadro(icono, fondo = fondoIcono, lado = 44.dp, radio = 14.dp, tamanoIcono = 24.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(titulo, style = Texto.LabelL, color = TintaBase)
            Text(descripcion, style = Texto.BodyS, color = TintaSuave)
        }
        Flecha()
    }
}

@Composable
fun FilaInterruptor(
    icono: Ico,
    titulo: String,
    encendido: Boolean,
    onCambio: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    descripcion: String? = null,
) {
    val apagado = !encendido
    Row(
        modifier.fillMaxWidth().padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(icono, tint = if (apagado) TintaSuave else com.example.access_tracker.ui.theme.Petroleo700, tamano = 22.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(titulo, style = Texto.LabelL, color = if (apagado) TintaSuave else TintaBase)
            if (descripcion != null) Text(descripcion, style = Texto.BodyS, color = TintaSuave)
        }
        Interruptor(encendido, onCambio)
    }
}

@Composable
fun ContadorMinimo(icono: Ico, valor: Int, onCambio: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(18.dp)).background(FondoArena).padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(icono, tint = TintaSuave, tamano = 18.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(30.dp).clip(CircleShape).background(FondoBlanco).clickable { if (valor > 0) onCambio(valor - 1) },
                contentAlignment = Alignment.Center,
            ) { Text("–", style = Texto.LabelL, color = TintaBase) }
            Box(Modifier.width(26.dp).height(30.dp), contentAlignment = Alignment.Center) {
                Text(valor.toString(), style = Texto.TitleS, color = TintaBase)
            }
            Box(
                Modifier.size(30.dp).clip(CircleShape).background(TintaBase).clickable { onCambio(valor + 1) },
                contentAlignment = Alignment.Center,
            ) { Icono(Ico.Plus, tint = FondoCrema, tamano = 16.dp) }
        }
    }
}

@Composable
fun EventoBitacora(
    icono: Ico,
    fondoIcono: Color,
    tipo: String,
    tono: TonoChip,
    titulo: String,
    detalle: String,
    modifier: Modifier = Modifier,
    motivo: String? = null,
    estancia: String? = null,
    estiloTitulo: androidx.compose.ui.text.TextStyle = Texto.LabelM,
) {
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(FondoBlanco).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconoCuadro(icono, fondo = fondoIcono, lado = 40.dp, radio = 14.dp, tamanoIcono = 24.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Chip(tipo, tono = tono, punto = false)
                if (estancia != null) {
                    Icono(Ico.Building, tint = TintaSuave, tamano = 14.dp)
                    Text(estancia, style = Texto.LabelS, color = TintaSuave)
                }
            }
            Text(titulo, style = estiloTitulo, color = TintaBase)
            Text(detalle, style = Texto.BodyS, color = TintaSuave)
            if (motivo != null) Text(motivo, style = Texto.BodyS, color = TintaBase)
        }
    }
}
