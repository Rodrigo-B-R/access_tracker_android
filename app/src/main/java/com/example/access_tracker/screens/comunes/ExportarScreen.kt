package com.example.access_tracker.screens.comunes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.access_tracker.components.CampoFecha
import com.example.access_tracker.components.CampoOpciones
import com.example.access_tracker.components.Casilla
import com.example.access_tracker.components.Divisor
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.GrupoFilas
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto

@Composable
fun ExportarScreen(onAtras: () -> Unit, onDescargar: () -> Unit, direccion: Boolean = false) {
    var periodo by remember { mutableIntStateOf(1) }
    val incluir = remember { mutableStateListOf(true, true, true, false) }
    val opciones = listOf(
        "Asistencias" to "Entradas y salidas, con autor y hora de captura",
        "Incidencias" to "Retardos y correcciones",
        "Ausencias" to "Faltas por categoría",
        "Altas, bajas y absentismo" to "Rotación del periodo",
    )
    PantallaBase(
        separacion = 18.dp,
        pie = { Boton("Descargar CSV", onDescargar, estilo = EstiloBoton.Oscuro) },
    ) {
        EncabezadoPantalla("Exportar", onAtras = onAtras, eyebrow = if (direccion) "Dirección" else "Estancia Cumbres")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Periodo", style = Texto.LabelM, color = TintaSuave)
            Segmentado(listOf("Semana", "Mes", "Personalizado"), periodo, { periodo = it })
        }
        CampoFecha("Mes", "Septiembre 2026", formato = "MMMM yyyy")
        if (direccion) {
            CampoOpciones(
                "Estancia", "Todas las estancias", listOf(
                    "Todas las estancias",
                    "Estancia Cumbres",
                    "Estancia San Nicolás",
                    "Estancia Apodaca",
                    "Estancia Guadalupe",
                    "Estancia Escobedo"
                ),
                inicio = { Icono(Ico.Building, tint = TintaSuave, tamano = 20.dp) },
            )
        }
        Text("Qué incluir", style = Texto.LabelM, color = TintaSuave)
        GrupoFilas {
            opciones.forEachIndexed { i, (titulo, detalle) ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(FondoArena)
                .padding(horizontal = 16.dp, vertical = 14.dp),
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
private fun ExportarPreview() {
    Access_trackerTheme { ExportarScreen({}, {}) }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun ExportarDireccionPreview() {
    Access_trackerTheme { ExportarScreen({}, {}, direccion = true) }
}
