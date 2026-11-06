package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.BotonCampana
import com.example.access_tracker.components.BotonCircular
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TituloPantalla
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto

private val Semana = listOf("L", "M", "M", "J", "V", "S", "D")
private const val HoyMiercoles = 2

private val Cuadricula = listOf(
    "Ana T." to "MMMM···",
    "Lucía H." to "M·MMMM·",
    "Carmen R." to "VVVV·V·",
    "Sofía D." to "MM!MM··",
    "Paola R." to "·MMMM·V",
    "Rosa M." to "MMMMM··",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResHorariosScreen(onAvisos: () -> Unit, onPersona: () -> Unit, barra: @Composable () -> Unit = {}) {
    PantallaBase(barra = barra, separacion = 16.dp) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { TituloPantalla("Horarios") }
            BotonCampana(3, onAvisos)
        }
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(FondoArena).padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BotonCircular(Ico.Back, {}, fondo = FondoBlanco, lado = 40.dp)
            Text("5 – 11 de octubre", style = Texto.LabelL, color = TintaBase, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            BotonCircular(Ico.Chevron, {}, fondo = FondoBlanco, lado = 40.dp)
        }
        Text("Cada semana repite la anterior. Toca el nombre de una persona para cambiar sus días y turnos.", style = Texto.BodyS, color = TintaTenue)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Chip("M · Matutino", tono = TonoChip.Info)
            Chip("V · Vespertino", tono = TonoChip.Alerta)
            Chip("Descanso", tono = TonoChip.Neutro)
            Chip("Sin cubrir", tono = TonoChip.Peligro)
        }
        Tarjeta(radio = 24.dp, relleno = 0.dp) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.width(76.dp).height(20.dp))
                    Semana.forEachIndexed { i, dia ->
                        Box(Modifier.weight(1f).height(20.dp), contentAlignment = Alignment.Center) {
                            Text(dia, style = Texto.LabelS, color = if (i == HoyMiercoles) TintaBase else TintaTenue)
                        }
                    }
                }
                Cuadricula.forEach { (nombre, turnos) ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.width(76.dp).height(36.dp).clickable(onClick = onPersona), contentAlignment = Alignment.CenterStart) {
                            Text(nombre, style = Texto.LabelM, color = TintaBase, maxLines = 1)
                        }
                        turnos.forEach { celda -> CeldaTurno(celda, Modifier.weight(1f)) }
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Rojo100).padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icono(Ico.Alert, tint = Rojo600, tamano = 22.dp)
            Text("Miércoles · Sofía Díaz: turno sin cubrir en Maternal A.", style = Texto.BodyMMedium, color = TintaBase, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CeldaTurno(codigo: Char, modifier: Modifier = Modifier) {
    val (fondo, tinta, texto) = when (codigo) {
        'M' -> Triple(Petroleo700, FondoCrema, "M")
        'V' -> Triple(Mostaza500, TintaBase, "V")
        '!' -> Triple(Rojo600, FondoBlanco, "!")
        else -> Triple(FondoArena, TintaTenue, "·")
    }
    Box(modifier.height(36.dp).clip(RoundedCornerShape(10.dp)).background(fondo), contentAlignment = Alignment.Center) {
        Text(texto, style = Texto.LabelM, color = tinta)
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResHorariosPreview() {
    Access_trackerTheme { ResHorariosScreen({}, {}) }
}
