package com.example.access_tracker.screens.empleada

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TarjetaCifra
import com.example.access_tracker.components.TituloPantalla
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100

/** Pestaña Asistencias: la empleada solo ve lo suyo. */
@Composable
fun AsistenciasScreen() {
    var periodo by remember { mutableIntStateOf(0) }
    PantallaBase(conBarra = true, separacion = 16.dp) {
        TituloPantalla("Mis asistencias")
        Segmentado(listOf("Esta semana", "Semana pasada", "Mes"), periodo, { periodo = it })
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaCifra("4", "días trabajados", Petroleo100, Modifier.weight(1f))
            TarjetaCifra("1", "retardo", Mostaza100, Modifier.weight(1f))
            TarjetaCifra("0", "faltas", Verde100, Modifier.weight(1f))
        }
        DiaAsistencia("MIÉ", "7", "Por sincronizar", TonoChip.Neutro, "Entrada 7:02 · Salida en curso", actual = true)
        DiaAsistencia("MAR", "6", "Retardo 12 min", TonoChip.Alerta, "Entrada 7:12 · Salida 15:01")
        DiaAsistencia("LUN", "5", "A tiempo", TonoChip.Exito, "Entrada 6:58 · Salida 15:03")
        DiaAsistencia("VIE", "2", "A tiempo", TonoChip.Exito, "Entrada 7:00 · Salida 15:00")
    }
}

@Composable
private fun DiaAsistencia(
    dia: String,
    numero: String,
    estado: String,
    tono: TonoChip,
    detalle: String,
    actual: Boolean = false,
) {
    Tarjeta(radio = 24.dp, relleno = 14.dp, borde = if (actual) Petroleo700 else null) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(
                    Modifier.height(56.dp).width(52.dp).clip(RoundedCornerShape(16.dp)).background(if (actual) Petroleo700 else FondoArena),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(dia, style = Texto.LabelS, color = if (actual) Mostaza500 else TintaSuave)
                    Text(numero, style = Texto.TitleM, color = if (actual) FondoCrema else TintaBase)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Chip(estado, tono = tono)
                    Text(detalle, style = Texto.BodyM, color = TintaBase)
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun AsistenciasPreview() {
    Access_trackerTheme { AsistenciasScreen() }
}
