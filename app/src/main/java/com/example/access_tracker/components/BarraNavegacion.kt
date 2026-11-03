package com.example.access_tracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.Texto

data class Pestana(
    val ruta: String,
    val etiqueta: String,
    val icono: Ico,
    val contador: Int? = null,
)

/** Barra flotante oscura; la pestaña activa se expande con etiqueta junto al icono. */
@Composable
fun BarraNavegacion(
    pestanas: List<Pestana>,
    activa: String?,
    onSeleccion: (Pestana) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .shadow(14.dp, RoundedCornerShape(36.dp), ambientColor = TintaBase, spotColor = TintaBase)
            .clip(RoundedCornerShape(36.dp))
            .background(TintaBase)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        pestanas.forEach { pestana ->
            val seleccionada = pestana.ruta == activa
            if (seleccionada) {
                val vertical = pestanas.size > 4
                PestanaActiva(pestana, vertical, onSeleccion, Modifier.weight(if (vertical) 1f else 2f))
            }
            else PestanaInactiva(pestana, onSeleccion, Modifier.weight(1f))
        }
    }
}

@Composable
private fun PestanaActiva(pestana: Pestana, vertical: Boolean, onSeleccion: (Pestana) -> Unit, modifier: Modifier) {
    val base = modifier
        .height(56.dp)
        .clip(RoundedCornerShape(28.dp))
        .background(Mostaza500)
        .clickable { onSeleccion(pestana) }
    if (vertical) {
        Box(base, contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Icono(pestana.icono, tint = TintaBase)
                Text(pestana.etiqueta, style = Texto.LabelS, color = TintaBase, maxLines = 1)
            }
            if (pestana.contador != null) Contador(pestana.contador, Modifier.align(Alignment.TopEnd).offset(x = (-2).dp, y = 2.dp), borde = Mostaza500)
        }
    } else {
        Row(
            modifier = base.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icono(pestana.icono, tint = TintaBase)
            Text(pestana.etiqueta, style = Texto.LabelM, color = TintaBase, maxLines = 1)
        }
    }
}

@Composable
private fun PestanaInactiva(pestana: Pestana, onSeleccion: (Pestana) -> Unit, modifier: Modifier) {
    Box(modifier = modifier.height(56.dp).clickable { onSeleccion(pestana) }, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icono(pestana.icono, tint = FondoCrema)
            Text(pestana.etiqueta, style = Texto.LabelS, color = FondoCrema, maxLines = 1)
        }
        if (pestana.contador != null) Contador(pestana.contador, Modifier.align(Alignment.TopEnd).offset(x = (-2).dp, y = 2.dp))
    }
}

@Composable
private fun Contador(valor: Int, modifier: Modifier = Modifier, borde: Color = TintaBase) {
    Box(
        modifier = modifier.size(20.dp).clip(CircleShape).background(borde).padding(2.dp).clip(CircleShape).background(Coral500),
        contentAlignment = Alignment.Center,
    ) {
        Text(valor.toString(), style = Texto.LabelS, color = TintaBase)
    }
}
