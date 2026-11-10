package com.example.access_tracker.screens.empleada

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

/** Una fila de la lista de asistencias. */
data class AsistenciaVista(
    val dia: String,
    val numero: String,
    val estado: String,
    val tono: TonoChip,
    val detalle: String,
    val actual: Boolean = false,
)

/** Version de demostracion (indice de pantallas): datos fijos. */
@Composable
fun AsistenciasScreen(barra: @Composable () -> Unit = {}) {
    var periodo by remember { mutableIntStateOf(0) }
    AsistenciasContenido(
        periodo = periodo,
        onPeriodo = { periodo = it },
        trabajados = 4, retardos = 1, faltas = 0,
        dias = listOf(
            AsistenciaVista("MIÉ", "7", "Por sincronizar", TonoChip.Neutro, "Entrada 7:02 · Salida en curso", actual = true),
            AsistenciaVista("MAR", "6", "Retardo 12 min", TonoChip.Alerta, "Entrada 7:12 · Salida 15:01"),
            AsistenciaVista("LUN", "5", "A tiempo", TonoChip.Exito, "Entrada 6:58 · Salida 15:03"),
            AsistenciaVista("VIE", "2", "A tiempo", TonoChip.Exito, "Entrada 7:00 · Salida 15:00"),
        ),
        barra = barra,
    )
}

@Composable
fun AsistenciasContenido(
    periodo: Int,
    onPeriodo: (Int) -> Unit,
    trabajados: Int,
    retardos: Int,
    faltas: Int,
    dias: List<AsistenciaVista>,
    barra: @Composable () -> Unit = {},
    cargando: Boolean = false,
) {
    PantallaBase(barra = barra, separacion = 16.dp) {
        TituloPantalla("Mis asistencias")
        Segmentado(listOf("Esta semana", "Semana pasada", "Mes"), periodo, onPeriodo)
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaCifra(trabajados.toString(), if (trabajados == 1) "día trabajado" else "días trabajados", Petroleo100, Modifier.weight(1f))
            TarjetaCifra(retardos.toString(), if (retardos == 1) "retardo" else "retardos", Mostaza100, Modifier.weight(1f))
            TarjetaCifra(faltas.toString(), if (faltas == 1) "falta" else "faltas", Verde100, Modifier.weight(1f))
        }
        if (cargando) {
            Text("Cargando…", style = Texto.BodyM, color = TintaSuave)
        } else if (dias.isEmpty()) {
            Text("No hay días con registro en este periodo.", style = Texto.BodyM, color = TintaSuave)
        }
        dias.forEach { d -> DiaAsistencia(d.dia, d.numero, d.estado, d.tono, d.detalle, d.actual) }
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
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier
                        .height(56.dp)
                        .width(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (actual) Petroleo700 else FondoArena),
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
