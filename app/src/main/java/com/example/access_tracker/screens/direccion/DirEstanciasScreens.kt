package com.example.access_tracker.screens.direccion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.access_tracker.components.DialogoFecha
import com.example.access_tracker.components.Ico
import com.example.access_tracker.components.MenuCuenta
import com.example.access_tracker.components.Icono
import com.example.access_tracker.components.PantallaBase
import com.example.access_tracker.ui.theme.Access_trackerTheme
import com.example.access_tracker.ui.theme.Coral500
import com.example.access_tracker.ui.theme.FondoArena
import com.example.access_tracker.ui.theme.FondoBlanco
import com.example.access_tracker.ui.theme.FondoCrema
import com.example.access_tracker.ui.theme.Petroleo100
import com.example.access_tracker.ui.theme.Petroleo500
import com.example.access_tracker.ui.theme.Petroleo700
import com.example.access_tracker.ui.theme.Rojo100
import com.example.access_tracker.ui.theme.Rojo600
import com.example.access_tracker.ui.theme.TintaBase
import com.example.access_tracker.ui.theme.TintaSuave
import com.example.access_tracker.ui.theme.Texto
import com.example.access_tracker.ui.theme.Verde100
import com.example.access_tracker.ui.theme.Verde600
import androidx.compose.ui.graphics.Color

@Composable
fun DirResumenScreen(
    onCambiarPassword: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    barra: @Composable () -> Unit = {}
) {
    var fecha by remember { mutableStateOf("Mié 7 oct") }
    var elegirFecha by remember { mutableStateOf(false) }
    if (elegirFecha) {
        DialogoFecha(
            formato = "EEE d MMM",
            onCerrar = { elegirFecha = false },
            onElegida = { fecha = it.replace(".", "") }
        )
    }
    PantallaBase(
        barra = barra,
        separacion = 18.dp,
        cabecera = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(Petroleo700)
                    .statusBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Dirección", style = Texto.LabelM, color = Petroleo100)
                        Text("Resumen", style = Texto.DisplayL, color = FondoCrema, maxLines = 1, softWrap = false)
                    }
                    MenuCuenta(onCambiarPassword, onCerrarSesion, oscuro = true)
                }
                Row(
                    Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Petroleo500)
                        .clickable { elegirFecha = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icono(Ico.Calendar, tint = FondoCrema, tamano = 20.dp)
                    Text(fecha, style = Texto.LabelL, color = FondoCrema, maxLines = 1, softWrap = false)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Cifra("3", "estancias\ncompletas", FondoBlanco, Modifier.weight(1f))
                    Cifra("2", "con\nfaltante", Coral500, Modifier.weight(1f))
                }
            }
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EstanciaEstado("Apodaca", "Falta 1 en Maternal A", completa = false, Modifier.weight(1f))
                EstanciaEstado("Escobedo", "Faltan 2 en Lactantes", completa = false, Modifier.weight(1f))
            }
            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EstanciaEstado("Cumbres", "Completa", completa = true, Modifier.weight(1f))
                EstanciaEstado("San Nicolás", "Completa", completa = true, Modifier.weight(1f))
            }
            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EstanciaEstado("Guadalupe", "Completa", completa = true, Modifier.weight(1f))
                Box(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Cifra(valor: String, etiqueta: String, fondo: Color, modifier: Modifier) {
    Row(
        modifier.clip(RoundedCornerShape(28.dp)).background(fondo).padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(valor, style = Texto.DisplayL, color = TintaBase)
        Text(etiqueta, style = Texto.LabelL, color = TintaBase)
    }
}

@Composable
private fun EstanciaEstado(nombre: String, estado: String, completa: Boolean, modifier: Modifier) {
    val forma = RoundedCornerShape(26.dp)
    Column(
        modifier = modifier
            .clip(forma)
            .background(if (completa) Verde100 else Rojo100)
            .border(2.dp, if (completa) Verde100 else Rojo600, forma)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(nombre, style = Texto.TitleS, color = TintaBase)
            if (completa) Box(Modifier.size(22.dp).clip(CircleShape).background(Verde600))
            else Box(Modifier.size(22.dp).clip(RoundedCornerShape(5.dp)).background(Rojo600))
        }
        Text(estado, style = Texto.LabelM, color = if (completa) Verde600 else Rojo600)
    }
}

@Composable
internal fun EtiquetaLectura(texto: String) {
    Row(
        Modifier.clip(RoundedCornerShape(20.dp)).background(FondoArena).padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(Ico.Eye, tint = TintaSuave, tamano = 16.dp)
        Text(texto, style = Texto.LabelS, color = TintaSuave)
    }
}

@Preview(showBackground = true, heightDp = 915, widthDp = 412)
@Composable
private fun DirResumenPreview() {
    Access_trackerTheme { DirResumenScreen() }
}
