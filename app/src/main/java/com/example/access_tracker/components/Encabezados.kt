package com.example.access_tracker.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.access_tracker.R
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Petroleo500
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun Logo(
    modifier: Modifier = Modifier,
    lado: Dp = 48.dp,
    sobreOscuro: Boolean = false,
) {
    if (sobreOscuro) {
        Box(
            modifier = modifier
                .size(lado)
                .clip(CircleShape)
                .background(FondoCrema)
                .padding(5.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo_sonrisas),
                contentDescription = "Sonrisas de Cristal",
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        Image(
            painter = painterResource(R.drawable.logo_sonrisas),
            contentDescription = "Sonrisas de Cristal",
            modifier = modifier.size(lado)
        )
    }
}

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

@Composable
fun BarraSuperiorLogo(
    avisos: Int,
    onAvisos: () -> Unit,
    modifier: Modifier = Modifier,
    oscuro: Boolean = false,
    cuenta: (@Composable () -> Unit)? = null,
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Logo(sobreOscuro = oscuro)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (oscuro) AvisosPill(avisos, onAvisos, fondo = Petroleo500, tinta = FondoCrema) else AvisosPill(avisos, onAvisos)
            cuenta?.invoke()
        }
    }
}

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
        IconButton(
            onClick = onAtras,
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = fondoBoton)
        ) {
            Icono(if (cerrar) Ico.X else Ico.Back, tint = TintaBase)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (eyebrow != null) Text(eyebrow.uppercase(), style = Texto.Eyebrow, color = TintaSuave)
            Text(titulo, style = if (grande) Texto.TitleL else Texto.TitleM, color = tinta)
        }
        accion?.invoke()
    }
}

@Composable
fun TituloConEyebrow(eyebrow: String, titulo: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(eyebrow.uppercase(), style = Texto.Eyebrow, color = TintaSuave)
        Text(titulo, style = Texto.TitleL, color = TintaBase)
    }
}

@Composable
fun TituloPantalla(titulo: String, modifier: Modifier = Modifier, subtitulo: String? = null) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(titulo, style = Texto.TitleL, color = TintaBase)
        if (subtitulo != null) Text(subtitulo, style = Texto.BodyM, color = TintaSuave)
    }
}

@Composable
fun Eyebrow(texto: String, modifier: Modifier = Modifier) {
    Text(texto.uppercase(), style = Texto.Eyebrow, color = TintaSuave, modifier = modifier)
}

@Composable
fun TextoCentrado(texto: String, modifier: Modifier = Modifier, color: Color = TintaSuave) {
    Text(texto, style = Texto.BodyM, color = color, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
}
