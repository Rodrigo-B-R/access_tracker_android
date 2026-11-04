package com.example.access_tracker.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoArenaFuerte
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto

/** Selector segmentado («Esta semana · Semana pasada · Mes»). */
@Composable
fun Segmentado(opciones: List<String>, seleccion: Int, onSeleccion: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(FondoArena).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        opciones.forEachIndexed { i, texto ->
            val activa = i == seleccion
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (activa) TintaBase else Color.Transparent)
                    .clickable { onSeleccion(i) }
                    .padding(horizontal = 4.dp, vertical = 11.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    texto, style = Texto.LabelM.copy(fontSize = 13.sp), color = if (activa) FondoCrema else TintaSuave,
                    maxLines = 1, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/** Filtro en píldora con menú («Todas», «Todas las salas»...). */
@Composable
fun FiltroDesplegable(inicial: String, opciones: List<String>, radio: androidx.compose.ui.unit.Dp = 26.dp, modifier: Modifier = Modifier) {
    var abierto by remember { mutableStateOf(false) }
    var elegido by remember { mutableStateOf(inicial) }
    Box(modifier) {
        Row(
            Modifier.clip(RoundedCornerShape(radio)).background(FondoArena).clickable { abierto = true }.padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(elegido, style = Texto.LabelM, color = TintaBase)
            Icono(Ico.Chevdown, tamano = 16.dp)
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }, containerColor = FondoBlanco) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, style = Texto.BodyL, color = TintaBase) },
                    onClick = { elegido = opcion; abierto = false },
                )
            }
        }
    }
}

/** Opción tipo botón (tipo de solicitud, Entrada/Salida...). Con [icono] muestra ese icono en lugar de la palomita. */
@Composable
fun ChipOpcion(
    texto: String,
    modifier: Modifier = Modifier,
    seleccionado: Boolean = false,
    bloqueado: Boolean = false,
    icono: Ico? = null,
    onClick: () -> Unit = {},
) {
    val forma = RoundedCornerShape(22.dp)
    val base = modifier
        .clip(forma)
        .background(
            when {
                seleccionado -> TintaBase
                bloqueado -> FondoArena
                else -> FondoBlanco
            },
        )
        .then(if (!seleccionado && !bloqueado) Modifier.border(BorderStroke(1.dp, FondoArenaFuerte), forma) else Modifier)
        .clickable(enabled = !bloqueado, onClick = onClick)
        .padding(horizontal = 10.dp, vertical = 11.dp)
    Row(base, horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
        // Con icono propio no hay palomita: la selección solo cambia el color.
        if (icono != null) Icono(icono, tint = if (seleccionado) Mostaza500 else TintaSuave, tamano = 18.dp)
        else if (seleccionado) Icono(Ico.Check, tint = Mostaza500, tamano = 14.dp)
        if (bloqueado) Icono(Ico.Lock, tint = TintaTenue, tamano = 14.dp)
        // Una sola línea: la palomita nunca debe empujar el texto a un salto de línea.
        Text(
            texto,
            style = Texto.LabelM,
            color = if (seleccionado) FondoCrema else if (bloqueado) TintaTenue else TintaBase,
            maxLines = 1,
            softWrap = false,
        )
    }
}

/** Cifra grande con etiqueta pequeña («4 · días trabajados»). */
@Composable
fun TarjetaCifra(valor: String, etiqueta: String, fondo: Color, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxHeight().clip(RoundedCornerShape(20.dp)).background(fondo).padding(horizontal = 14.dp, vertical = 12.dp)) {
        Text(valor, style = Texto.TitleL, color = TintaBase)
        Text(etiqueta, style = Texto.BodyS, color = TintaSuave)
    }
}

/** Píldora de estado con icono (zona válida, sincronizado...). */
@Composable
fun PildoraEstado(texto: String, icono: Ico, fondo: Color, tinta: Color, modifier: Modifier = Modifier, tintaIcono: Color = tinta) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(20.dp)).background(fondo).padding(horizontal = 14.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(icono, tint = tintaIcono, tamano = 16.dp)
        Text(texto, style = Texto.LabelM, color = tinta, maxLines = 1)
    }
}

/** Botón circular grande de Checar con halo. */
@Composable
fun BotonChecada(
    texto: String,
    icono: Ico,
    colorBoton: Color,
    colorHalo: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(Modifier.size(256.dp).clip(CircleShape).background(colorHalo), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(216.dp)
                    .shadow(16.dp, CircleShape, ambientColor = colorBoton, spotColor = colorBoton)
                    .clip(CircleShape)
                    .background(colorBoton)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icono(icono, tint = TintaBase, tamano = 40.dp)
                    Text(texto, style = Texto.TitleL, color = TintaBase, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

/** Interruptor de 52×32: petróleo con perilla mostaza cuando está encendido. */
@Composable
fun Interruptor(encendido: Boolean, onCambio: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(52.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (encendido) Petroleo700 else FondoArena)
            .clickable { onCambio(!encendido) }
            .padding(4.dp),
        contentAlignment = if (encendido) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(Modifier.size(24.dp).clip(CircleShape).background(if (encendido) Mostaza500 else FondoBlanco))
    }
}

/** Barra de progreso en segmentos (confirmaciones de lectura). */
@Composable
fun BarraSegmentos(total: Int, llenos: Int, colorLleno: Color, colorVacio: Color, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(total) { i ->
            Box(Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(if (i < llenos) colorLleno else colorVacio))
        }
    }
}

/** Botón flotante con icono y texto («+ Nueva», «+ Reportar»). */
@Composable
fun BotonFlotante(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, icono: Ico = Ico.Plus) {
    Row(
        modifier = modifier
            .height(60.dp)
            .shadow(12.dp, RoundedCornerShape(30.dp), ambientColor = TintaBase, spotColor = TintaBase)
            .clip(RoundedCornerShape(30.dp))
            .background(Mostaza500)
            .clickable(onClick = onClick)
            .padding(start = 18.dp, end = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(icono, tint = TintaBase)
        Text(texto, style = Texto.LabelL, color = TintaBase)
    }
}
