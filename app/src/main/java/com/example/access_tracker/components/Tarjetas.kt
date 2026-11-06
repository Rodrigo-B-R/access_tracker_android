package com.example.access_tracker.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Mostaza500
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.Texto

@Composable
fun Tarjeta(
    modifier: Modifier = Modifier,
    fondo: Color = FondoBlanco,
    radio: Dp = 22.dp,
    relleno: Dp = 16.dp,
    borde: Color? = null,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(radio),
        colors = CardDefaults.cardColors(containerColor = fondo),
        border = if (borde != null) BorderStroke(2.dp, borde) else null
    ) {
        Column(modifier = Modifier.padding(relleno)) {
            contenido()
        }
    }
}

@Composable
fun IconoCuadro(
    icono: Ico,
    modifier: Modifier = Modifier,
    fondo: Color = Mostaza500,
    tint: Color = TintaBase,
    lado: Dp = 64.dp,
    radio: Dp = 22.dp,
    tamanoIcono: Dp = 30.dp,
) {
    Box(
        modifier = modifier
            .size(lado)
            .clip(RoundedCornerShape(radio))
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Icono(icono, tint = tint, tamano = tamanoIcono)
    }
}

@Composable
fun TarjetaDato(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier,
    fondo: Color = Petroleo700,
    tinta: Color = FondoCrema,
    estiloValor: TextStyle = Texto.TitleL,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(fondo)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Text(titulo, style = Texto.LabelM, color = tinta)
        Text(valor, style = estiloValor, color = tinta, maxLines = 1, softWrap = false)
    }
}

@Composable
fun Nota(
    texto: String,
    modifier: Modifier = Modifier,
    icono: Ico = Ico.Info,
    fondo: Color = Petroleo100,
    tint: Color = Petroleo700,
    colorTexto: Color = TintaBase,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(fondo)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icono(icono, tint = tint, tamano = 20.dp)
        Text(texto, style = Texto.BodyM, color = colorTexto, modifier = Modifier.weight(1f))
    }
}
