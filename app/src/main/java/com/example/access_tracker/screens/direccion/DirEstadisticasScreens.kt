package com.example.access_tracker.screens.direccion

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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.FiltroDesplegable
import com.example.access_tracker.components.EventoBitacora
import com.example.access_tracker.components.Eyebrow
import com.example.access_tracker.components.FiltroPill
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.MenuCuenta
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100

/** Altas y bajas de una estancia para la grafica de barras. */
data class BarrasEstancia(val nombre: String, val altas: Int, val bajas: Int)

@Composable
fun DirEstadisticasScreen(
    onExportar: () -> Unit,
    onCambiarPassword: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    barra: @Composable () -> Unit = {}
) {
    DirEstadisticasContenido(
        periodo = 0, onPeriodo = {},
        estancias = listOf("Todas", "San Nicolás", "Centro", "Las Flores", "Norte"), onEstancia = {},
        altas = "6", bajas = "4", insuficiente = "9 días", absentismo = "4.2 %",
        barras = listOf(
            BarrasEstancia("Cumbres", 2, 1), BarrasEstancia("S. Nicolás", 1, 0),
            BarrasEstancia("Apodaca", 1, 2), BarrasEstancia("Guadalupe", 2, 0), BarrasEstancia("Escobedo", 0, 1),
        ),
        onExportar = onExportar, onCambiarPassword = onCambiarPassword, onCerrarSesion = onCerrarSesion,
        barra = barra,
    )
}

@Composable
fun DirEstadisticasContenido(
    periodo: Int,
    onPeriodo: (Int) -> Unit,
    estancias: List<String>,
    onEstancia: (Int) -> Unit,
    altas: String,
    bajas: String,
    insuficiente: String,
    absentismo: String,
    barras: List<BarrasEstancia>,
    onExportar: () -> Unit,
    onCambiarPassword: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    barra: @Composable () -> Unit = {},
    mensaje: String? = null,
) {
    PantallaBase(barra = barra, separacion = 14.dp) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Eyebrow("Dirección")
                Text("Estadísticas", style = Texto.TitleL, color = TintaBase)
            }
            EtiquetaLectura("Solo lectura")
            MenuCuenta(onCambiarPassword, onCerrarSesion)
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Segmentado(listOf("Mes", "Trim.", "Año"), periodo, onPeriodo, Modifier.weight(1f))
            FiltroDesplegable(estancias.first(), estancias, radio = 24.dp, onElegido = onEstancia)
        }
        if (mensaje != null) Text(mensaje, style = Texto.BodyM, color = TintaSuave)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Kpi(Ico.Userplus, "Altas", altas, Verde100, Modifier.weight(1f))
                Kpi(Ico.Userminus, "Bajas", bajas, Coral100, Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Kpi(Ico.Alert, "Personal insuficiente", insuficiente, Rojo100, Modifier.weight(1f))
                Kpi(Ico.Chart, "Absentismo", absentismo, Petroleo100, Modifier.weight(1f))
            }
        }
        Tarjeta(radio = 24.dp, relleno = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Altas y bajas por estancia", style = Texto.LabelL, color = TintaBase)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Leyenda(Petroleo700, "Altas")
                        Leyenda(Coral500, "Bajas")
                    }
                }
                val maximo = (barras.maxOfOrNull { maxOf(it.altas, it.bajas) } ?: 0).coerceAtLeast(1)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    barras.forEach { b ->
                        GrupoBarras(
                            b.nombre,
                            (4 + 64 * b.altas / maximo).dp,
                            (4 + 64 * b.bajas / maximo).dp,
                            Modifier.weight(1f),
                            altasVacio = b.altas == 0,
                            bajasVacio = b.bajas == 0,
                        )
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Petroleo700)
                .clickable(onClick = onExportar)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icono(Ico.Download, tint = Mostaza500, tamano = 20.dp)
            Text("Exportar reporte CSV", style = Texto.LabelL, color = FondoCrema, modifier = Modifier.weight(1f))
            Icono(Ico.Chevron, tint = FondoCrema, tamano = 20.dp)
        }
    }
}

@Composable
private fun Kpi(icono: Ico, etiqueta: String, valor: String, fondo: Color, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(22.dp)).background(fondo).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icono(icono, tint = TintaBase, tamano = 16.dp)
            Text(etiqueta, style = Texto.LabelS, color = TintaBase)
        }
        Text(valor, style = Texto.TitleL, color = TintaBase)
    }
}

@Composable
private fun Leyenda(color: Color, texto: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).background(color))
        Text(texto, style = Texto.BodyS, color = TintaSuave)
    }
}

@Composable
private fun GrupoBarras(
    nombre: String,
    altas: Dp,
    bajas: Dp,
    modifier: Modifier = Modifier,
    altasVacio: Boolean = false,
    bajasVacio: Boolean = false,
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            Modifier.width(40.dp).height(76.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                Modifier
                    .width(14.dp)
                    .height(altas)
                    .alpha(if (altasVacio) 0.25f else 1f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Petroleo700)
            )
            Box(
                Modifier
                    .width(14.dp)
                    .height(bajas)
                    .alpha(if (bajasVacio) 0.25f else 1f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Coral500)
            )
        }
        Text(nombre, style = Texto.BodyS, color = TintaSuave, maxLines = 1)
    }
}

/** Un renglon de la bitacora ya traducido para pintar. */
data class EventoVista(
    val icono: Ico, val fondo: Color, val tipo: String, val tono: TonoChip,
    val titulo: String, val detalle: String, val estancia: String? = null,
)

@Composable
fun DirBitacoraScreen(
    onCambiarPassword: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    barra: @Composable () -> Unit = {}
) {
    DirBitacoraContenido(
        filtros = listOf("Todas", "Cumbres", "San Nicolás", "Apodaca", "Guadalupe", "Escobedo"),
        filtro = 0, onFiltro = {},
        eventos = listOf(
            EventoVista(Ico.Fingerprint, Petroleo100, "Checada", TonoChip.Info, "Entrada registrada",
                "Registrado por Ana Torres · 7 oct 7:00", "Cumbres"),
            EventoVista(Ico.Alert, Coral100, "Ausencia", TonoChip.Coral, "Paola Reyes · Enfermedad",
                "Registrado por Responsable · 7 oct 8:02", "Cumbres"),
            EventoVista(Ico.Swap, Mostaza100, "Baja", TonoChip.Alerta, "Traslado a Estancia San Nicolás",
                "Registrado por Responsable · 6 oct 12:15", "Apodaca"),
            EventoVista(Ico.Fingerprint, Petroleo100, "Checada", TonoChip.Info, "Salida registrada",
                "Registrado por Rosa Medina · 6 oct 15:01", "Escobedo"),
        ),
        onCambiarPassword = onCambiarPassword, onCerrarSesion = onCerrarSesion, barra = barra,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DirBitacoraContenido(
    filtros: List<String>,
    filtro: Int,
    onFiltro: (Int) -> Unit,
    eventos: List<EventoVista>,
    onCambiarPassword: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    barra: @Composable () -> Unit = {},
    mensaje: String? = null,
) {
    PantallaBase(barra = barra, separacion = 14.dp) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Eyebrow("Dirección")
                Text("Bitácora", style = Texto.TitleL, color = TintaBase)
                Text("Elige una estancia o consulta todas", style = Texto.BodyM, color = TintaSuave)
            }
            EtiquetaLectura("Solo lectura")
            MenuCuenta(onCambiarPassword, onCerrarSesion)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filtros.forEachIndexed { i, t ->
                FiltroPill(t, activo = filtro == i, onClick = { onFiltro(i) })
            }
        }
        if (mensaje != null) Text(mensaje, style = Texto.BodyM, color = TintaSuave)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            eventos.forEach {
                EventoBitacora(
                    it.icono, it.fondo, it.tipo, it.tono, it.titulo, it.detalle,
                    estancia = it.estancia, estiloTitulo = Texto.LabelL,
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun DirEstadisticasPreview() {
    Access_trackerTheme { DirEstadisticasScreen({}) }
}
