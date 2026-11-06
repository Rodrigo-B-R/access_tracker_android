package com.example.access_tracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto

@Composable
fun BotonCampana(contador: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.size(48.dp)) {
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(FondoArena).clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) { Icono(Ico.Bell) }
        if (contador > 0) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(FondoCrema)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(Coral500),
                contentAlignment = Alignment.Center,
            ) { Text(contador.toString(), style = Texto.LabelS, color = TintaBase) }
        }
    }
}

@Composable
fun TarjetaSala(
    nombre: String,
    detalle: String,
    presentes: Int,
    total: Int,
    modifier: Modifier = Modifier,
    faltante: Boolean = false,
    textoAccion: String? = null,
    onAccion: () -> Unit = {},
    onClick: (() -> Unit)? = null,
) {
    val tinta = if (faltante) TintaBase else Petroleo700
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(if (faltante) Coral500 else FondoArena)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(nombre, style = Texto.TitleS, color = TintaBase)
                Text(detalle, style = if (faltante) Texto.LabelM else Texto.BodyM, color = if (faltante) TintaBase else TintaSuave)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(total) { i -> CuadroPersona(presente = i < presentes, color = tinta) }
            }
        }
        if (faltante && textoAccion != null) {
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(FondoBlanco).clickable(onClick = onAccion).padding(horizontal = 16.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icono(Ico.Flag, tamano = 20.dp)
                Text(textoAccion, style = Texto.LabelL, color = TintaBase)
            }
        }
    }
}

@Composable
fun CuadroPersona(presente: Boolean, color: Color, lado: Dp = 24.dp, radio: Dp = 8.dp, colorFaltante: Color = color) {
    if (presente) {
        Box(Modifier.size(lado).clip(RoundedCornerShape(radio)).background(color))
    } else {
        Box(Modifier.size(lado).border(2.dp, colorFaltante, RoundedCornerShape(radio)))
    }
}

@Composable
fun SalaMini(
    nombre: String,
    presentes: Int,
    total: Int,
    modifier: Modifier = Modifier,
    bajoMinimo: Boolean = false,
) {
    val forma = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .clip(forma)
            .background(if (bajoMinimo) Rojo100 else FondoArena)
            .border(2.dp, if (bajoMinimo) Rojo600 else Color.Transparent, forma)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(nombre, style = Texto.LabelL, color = if (bajoMinimo) Rojo600 else TintaBase)
            Text("$presentes/$total", style = Texto.LabelL, color = if (bajoMinimo) Rojo600 else TintaSuave)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(total) { i ->
                CuadroPersona(
                    presente = i < presentes,
                    color = if (bajoMinimo) TintaBase else Petroleo700,
                    lado = 22.dp, radio = 7.dp,
                    colorFaltante = if (bajoMinimo) Rojo600 else Petroleo700,
                )
            }
        }
    }
}
