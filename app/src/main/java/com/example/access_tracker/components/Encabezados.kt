package com.example.access_tracker.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.access_tracker.R
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo500
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.TintaTenue
import com.example.access_tracker.ui.theme.Texto

/** Logo de Sonrisas de Cristal. Sobre fondo oscuro va en un círculo claro para que su texto negro se lea. */
@Composable
fun Logo(
    modifier: Modifier = Modifier,
    lado: Dp = 48.dp,
    sobreOscuro: Boolean = false,
) {
    Image(
        painter = painterResource(R.drawable.logo_sonrisas),
        contentDescription = "Sonrisas de Cristal",
        modifier = modifier
            .size(lado)
            .then(if (sobreOscuro) Modifier.clip(CircleShape).background(FondoCrema).padding(lado * 0.06f) else Modifier),
    )
}

/** Botón «N avisos» de la barra superior de la Empleada/o. */
@Composable
fun AvisosPill(
    cantidad: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fondo: Color = FondoArena,
    tinta: Color = TintaBase,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(fondo)
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 18.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(Ico.Bell, tint = tinta, tamano = 22.dp)
        Text(if (cantidad == 1) "1 aviso" else "$cantidad avisos", style = Texto.LabelL, color = tinta)
    }
}

/** Logo a la izquierda y avisos a la derecha. `oscuro` es la variante sobre fondo petróleo. */
@Composable
fun BarraSuperiorLogo(avisos: Int, onAvisos: () -> Unit, modifier: Modifier = Modifier, oscuro: Boolean = false) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Logo(sobreOscuro = oscuro)
        if (oscuro) AvisosPill(avisos, onAvisos, fondo = Petroleo500, tinta = FondoCrema) else AvisosPill(avisos, onAvisos)
    }
}

/** Encabezado de pantallas secundarias: botón circular (volver o cerrar) y título. */
@Composable
fun EncabezadoPantalla(
    titulo: String,
    onAtras: () -> Unit,
    modifier: Modifier = Modifier,
    cerrar: Boolean = false,
    grande: Boolean = false,
    fondoBoton: Color = FondoArena,
    tinta: Color = TintaBase,
    eyebrow: String? = null,
    accion: (@Composable () -> Unit)? = null,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(fondoBoton).clickable(onClick = onAtras),
            contentAlignment = Alignment.Center,
        ) { Icono(if (cerrar) Ico.X else Ico.Back, tint = TintaBase) }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (eyebrow != null) Text(eyebrow.uppercase(), style = Texto.Eyebrow, color = TintaSuave)
            Text(titulo, style = if (grande) Texto.TitleL else Texto.TitleM, color = tinta)
        }
        accion?.invoke()
    }
}

/** «COMPONENTE» + título grande. */
@Composable
fun TituloConEyebrow(eyebrow: String, titulo: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(eyebrow.uppercase(), style = Texto.Eyebrow, color = TintaSuave)
        Text(titulo, style = Texto.TitleL, color = TintaBase)
    }
}

/** Título de pestaña con subtítulo suave debajo. */
@Composable
fun TituloPantalla(titulo: String, modifier: Modifier = Modifier, subtitulo: String? = null) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(titulo, style = Texto.TitleL, color = TintaBase)
        if (subtitulo != null) Text(subtitulo, style = Texto.BodyM, color = TintaSuave)
    }
}

/** Etiqueta de sección en mayúsculas pequeñas («Anteriores», «Turnos sin cubrir»). */
@Composable
fun Eyebrow(texto: String, modifier: Modifier = Modifier) {
    Text(texto.uppercase(), style = Texto.Eyebrow, color = TintaSuave, modifier = modifier)
}

@Composable
fun TextoCentrado(texto: String, modifier: Modifier = Modifier, color: Color = TintaSuave) {
    Text(texto, style = Texto.BodyM, color = color, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
}
