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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.access_tracker.components.Boton
import com.example.access_tracker.components.CampoSelector
import com.example.access_tracker.components.FiltroDesplegable
import com.example.access_tracker.components.Casilla
import com.example.access_tracker.components.Chip
import com.example.access_tracker.components.Divisor
import com.example.access_tracker.components.EncabezadoPantalla
import com.example.access_tracker.components.EstiloBoton
import com.example.access_tracker.components.EventoBitacora
import com.example.access_tracker.components.Eyebrow
import com.example.access_tracker.components.FiltroPill
import com.example.access_tracker.components.GrupoFilas
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.MenuCuenta
import com.example.access_tracker.components.IconoCuadro
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.Nota
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.components.Segmentado
import com.example.access_tracker.components.Tarjeta
import com.example.access_tracker.components.TonoChip
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral100
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza100
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100

/** Estadísticas de toda la fundación para el periodo elegido. Solo lectura. */
@Composable
fun DirEstadisticasScreen(onExportar: () -> Unit, onCambiarPassword: () -> Unit = {}, onCerrarSesion: () -> Unit = {}) {
    var periodo by remember { mutableIntStateOf(0) }
    PantallaBase(conBarra = true, separacion = 14.dp) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Eyebrow("Dirección")
                Text("Estadísticas", style = Texto.TitleL, color = TintaBase)
            }
            EtiquetaLectura("Solo lectura")
            MenuCuenta(onCambiarPassword, onCerrarSesion)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Segmentado(listOf("Mes", "Trim.", "Año"), periodo, { periodo = it }, Modifier.weight(1f))
            FiltroDesplegable("Todas", listOf("Todas", "San Nicolás", "Centro", "Las Flores", "Norte"), radio = 24.dp)
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Kpi(Ico.Userplus, "Altas", "6", Verde100, Modifier.weight(1f))
                Kpi(Ico.Userminus, "Bajas", "4", Coral100, Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Kpi(Ico.Alert, "Personal insuficiente", "9 días", Rojo100, Modifier.weight(1f))
                Kpi(Ico.Chart, "Absentismo", "4.2 %", Petroleo100, Modifier.weight(1f))
            }
        }
        Tarjeta(radio = 24.dp, relleno = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Altas y bajas por estancia", style = Texto.LabelL, color = TintaBase)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Leyenda(Petroleo700, "Altas")
                        Leyenda(Coral500, "Bajas")
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                    GrupoBarras("Cumbres", 68.dp, 34.dp, Modifier.weight(1f))
                    GrupoBarras("S. Nicolás", 34.dp, 4.dp, Modifier.weight(1f), bajasVacio = true)
                    GrupoBarras("Apodaca", 34.dp, 68.dp, Modifier.weight(1f))
                    GrupoBarras("Guadalupe", 68.dp, 4.dp, Modifier.weight(1f), bajasVacio = true)
                    GrupoBarras("Escobedo", 4.dp, 34.dp, Modifier.weight(1f), altasVacio = true)
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Petroleo700).clickable(onClick = onExportar).padding(horizontal = 18.dp, vertical = 14.dp),
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
    Column(modifier.clip(RoundedCornerShape(22.dp)).background(fondo).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.width(40.dp).height(76.dp), horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.Bottom) {
            Box(Modifier.width(14.dp).height(altas).alpha(if (altasVacio) 0.25f else 1f).clip(RoundedCornerShape(5.dp)).background(Petroleo700))
            Box(Modifier.width(14.dp).height(bajas).alpha(if (bajasVacio) 0.25f else 1f).clip(RoundedCornerShape(5.dp)).background(Coral500))
        }
        Text(nombre, style = Texto.BodyS, color = TintaSuave, maxLines = 1)
    }
}

/** Bitácora de la fundación con selector por estancia. Solo lectura. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DirBitacoraScreen(onCambiarPassword: () -> Unit = {}, onCerrarSesion: () -> Unit = {}) {
    var filtro by remember { mutableIntStateOf(0) }
    PantallaBase(conBarra = true, separacion = 14.dp) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Eyebrow("Dirección")
                Text("Bitácora", style = Texto.TitleL, color = TintaBase)
                Text("Elige una estancia o consulta todas", style = Texto.BodyM, color = TintaSuave)
            }
            EtiquetaLectura("Solo lectura")
            MenuCuenta(onCambiarPassword, onCerrarSesion)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todas", "Cumbres", "San Nicolás", "Apodaca", "Guadalupe", "Escobedo").forEachIndexed { i, t ->
                FiltroPill(t, activo = filtro == i, onClick = { filtro = i })
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            EventoBitacora(
                Ico.Fingerprint, Petroleo100, "Checada", TonoChip.Info, "Entrada registrada",
                "Registrado por Ana Torres · 7 oct 7:00", estancia = "Cumbres", estiloTitulo = Texto.LabelL,
            )
            EventoBitacora(
                Ico.Alert, Coral100, "Ausencia", TonoChip.Coral, "Paola Reyes · Enfermedad",
                "Registrado por Responsable · 7 oct 8:02", estancia = "Cumbres", estiloTitulo = Texto.LabelL,
            )
            EventoBitacora(
                Ico.Swap, Mostaza100, "Baja", TonoChip.Alerta, "Traslado a Estancia San Nicolás",
                "Registrado por Responsable · 6 oct 12:15", estancia = "Apodaca", estiloTitulo = Texto.LabelL,
            )
            EventoBitacora(
                Ico.Fingerprint, Petroleo100, "Checada", TonoChip.Info, "Salida registrada",
                "Registrado por Rosa Medina · 6 oct 15:01", estancia = "Escobedo", estiloTitulo = Texto.LabelL,
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun DirEstadisticasPreview() {
    Access_trackerTheme { DirEstadisticasScreen({}) }
}
