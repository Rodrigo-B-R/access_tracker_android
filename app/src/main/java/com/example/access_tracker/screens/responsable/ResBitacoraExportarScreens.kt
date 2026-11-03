package com.example.access_tracker.screens.responsable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.CampoSelector
import com.example.access_tracker.components.Casilla
import com.example.access_tracker.components.Divisor
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.EventoBitacora
import com.example.access_tracker.components.FiltroPill
import com.example.access_tracker.components.GrupoFilas
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100

/** Bitácora de la estancia: quién hizo qué y cuándo. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResBitacoraScreen(onAtras: () -> Unit) {
    var filtro by remember { mutableIntStateOf(0) }
    PantallaBase(separacion = 16.dp) {
        EncabezadoPantalla("Bitácora", onAtras = onAtras, eyebrow = "Estancia Cumbres")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todo", "Checadas", "Correcciones", "Altas y bajas", "Permisos").forEachIndexed { i, t ->
                FiltroPill(t, activo = filtro == i, onClick = { filtro = i })
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            EventoBitacora(
                Ico.Fingerprint, Petroleo100, "Checada", TonoChip.Info,
                "Entrada de Ana Torres · 7:00", "Registrado por Ana Torres · 7 oct 7:00",
            )
            EventoBitacora(
                Ico.Edit, Coral100, "Corrección", TonoChip.Coral,
                "Entrada de Ana Torres: 7:25 → 7:00", "Pidió: Sofía Díaz · Autorizó: Responsable · 7 oct 8:02",
                motivo = "Motivo: olvidó checar al llegar",
            )
            EventoBitacora(
                Ico.Userplus, Verde100, "Alta", TonoChip.Exito,
                "Marta Gil Soto · Maestra titular", "Registrado por Responsable · 6 oct 9:30",
            )
            EventoBitacora(
                Ico.Shield, Mostaza100, "Permisos", TonoChip.Alerta,
                "Asistente: reportar salidas anticipadas → apagado", "Registrado por Responsable · 5 oct 16:10",
            )
        }
    }
}

/** Descarga un CSV con asistencias, incidencias, ausencias y movimientos. */
@Composable
fun ResExportarScreen(onAtras: () -> Unit, onDescargar: () -> Unit) {
    var periodo by remember { mutableIntStateOf(1) }
    val incluir = remember { mutableStateListOf(true, true, true, false) }
    val opciones = listOf(
        "Asistencias" to "Entradas y salidas por día",
        "Incidencias" to "Retardos y correcciones",
        "Ausencias" to "Faltas por categoría",
        "Movimientos de personal" to "Altas, bajas y traslados",
    )
    PantallaBase(
        separacion = 18.dp,
        pie = { Boton("Descargar CSV", onDescargar, estilo = EstiloBoton.Oscuro) },
    ) {
        EncabezadoPantalla("Exportar", onAtras = onAtras, eyebrow = "Estancia Cumbres")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Periodo", style = Texto.LabelM, color = TintaSuave)
            Segmentado(listOf("Semana", "Mes", "Personalizado"), periodo, { periodo = it })
        }
        CampoSelector("Mes", "Septiembre 2026", icono = Ico.Calendar)
        Text("Qué incluir", style = Texto.LabelM, color = TintaSuave)
        GrupoFilas {
            opciones.forEachIndexed { i, (titulo, detalle) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(titulo, style = Texto.LabelL, color = TintaBase)
                        Text(detalle, style = Texto.BodyS, color = TintaSuave)
                    }
                    Casilla(incluir[i], onClick = { incluir[i] = !incluir[i] })
                }
                if (i < opciones.lastIndex) Divisor()
            }
        }
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(FondoArena).padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconoCuadro(Ico.File, fondo = FondoBlanco, lado = 40.dp, radio = 14.dp, tamanoIcono = 24.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Formato CSV", style = Texto.LabelL, color = TintaBase)
                Text("Se comparte con el menú de Android", style = Texto.BodyS, color = TintaSuave)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResBitacoraPreview() {
    Access_trackerTheme { ResBitacoraScreen({}) }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ResExportarPreview() {
    Access_trackerTheme { ResExportarScreen({}, {}) }
}
