package com.example.access_tracker.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.unit.Dp

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

@Composable
fun FiltroDesplegable(inicial: String, opciones: List<String>, radio: Dp = 26.dp, modifier: Modifier = Modifier) {
    var abierto by remember { mutableStateOf(false) }
    var elegido by remember { mutableStateOf(inicial) }
    Box(modifier) {
        Row(
            Modifier
                .clip(RoundedCornerShape(radio))
                .background(FondoArena)
                .clickable { abierto = true }
                .padding(horizontal = 14.dp, vertical = 13.dp),
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
        .border(1.dp, if (seleccionado || bloqueado) Color.Transparent else FondoArenaFuerte, forma)
        .clickable(enabled = !bloqueado, onClick = onClick)
        .padding(horizontal = 10.dp, vertical = 11.dp)
    Row(
        base,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null) Icono(icono, tint = if (seleccionado) Mostaza500 else TintaSuave, tamano = 18.dp)
        else if (seleccionado) Icono(Ico.Check, tint = Mostaza500, tamano = 14.dp)
        if (bloqueado) Icono(Ico.Lock, tint = TintaTenue, tamano = 14.dp)
        Text(
            texto,
            style = Texto.LabelM,
            color = if (seleccionado) FondoCrema else if (bloqueado) TintaTenue else TintaBase,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
fun TarjetaCifra(valor: String, etiqueta: String, fondo: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(fondo)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(valor, style = Texto.TitleL, color = TintaBase)
        Text(etiqueta, style = Texto.BodyS, color = TintaSuave)
    }
}

@Composable
fun PildoraEstado(
    texto: String,
    icono: Ico,
    fondo: Color,
    tinta: Color,
    modifier: Modifier = Modifier,
    tintaIcono: Color = tinta
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(fondo)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(icono, tint = tintaIcono, tamano = 16.dp)
        Text(texto, style = Texto.LabelM, color = tinta, maxLines = 1)
    }
}

@Composable
fun BotonChecada(
    texto: String,
    icono: Ico,
    colorBoton: Color,
    colorHalo: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
) {
    Box(modifier.fillMaxWidth().alpha(if (habilitado) 1f else 0.5f), contentAlignment = Alignment.Center) {
        Box(Modifier.size(256.dp).clip(CircleShape).background(colorHalo), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(216.dp)
                    .shadow(16.dp, CircleShape, ambientColor = colorBoton, spotColor = colorBoton)
                    .clip(CircleShape)
                    .background(colorBoton)
                    .clickable(enabled = habilitado, onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icono(icono, tint = TintaBase, tamano = 40.dp)
                    Text(texto, style = Texto.TitleL, color = TintaBase, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun Interruptor(encendido: Boolean, onCambio: (Boolean) -> Unit) {
    Switch(
        checked = encendido,
        onCheckedChange = onCambio,
        colors = SwitchDefaults.colors(
            checkedTrackColor = Petroleo700,
            checkedThumbColor = Mostaza500,
            uncheckedTrackColor = FondoArena,
            uncheckedThumbColor = FondoBlanco,
            uncheckedBorderColor = FondoArenaFuerte
        )
    )
}

@Composable
fun BarraSegmentos(total: Int, llenos: Int, colorLleno: Color, colorVacio: Color, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(total) { i ->
            Box(
                Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (i < llenos) colorLleno else colorVacio)
            )
        }
    }
}

@Composable
fun BotonFlotante(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, icono: Ico = Ico.Plus) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        containerColor = Mostaza500,
        contentColor = TintaBase,
        icon = { Icono(icono, tint = TintaBase) },
        text = { Text(texto, style = Texto.LabelL) }
    )
}
